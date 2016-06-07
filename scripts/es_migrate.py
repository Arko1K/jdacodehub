import json
import requests
import time

HOST_TO = 'localhost'
INDEX_TO = 'jda-submission'
TYPE = 'submission'
RECREATE_INDEX = True
FILE_PATH = "./coders.sql"


def get_elements(data):
    elements = []
    str = ""
    break_at_comma = True
    prev_char = ''
    for c in data:
        if prev_char != '\\' and c == "'":
            break_at_comma = not break_at_comma
        else:
            if break_at_comma and c == ',':
                elements.append(str)
                str = ""
            else:
                str += c
        prev_char = c
    elements.append(str)
    return elements

def get_complete_data(data):
    elements = []
    complete_data = []
    str = ""
    break_at_position = True
    prev_char = ''
    for c in data:
        if prev_char != '\\' and c == "'":
            break_at_position = not break_at_position
        else:
            if break_at_position and c == ',':
                if (len(elements) == 0):
                    if (str[0] == '('):
                        str = str[1:]
                if (len(elements) == 5):
                    if (str[-1] == ')'):
                        str = str[:-1]
                elements.append(str)
                str = ""
                break_at_position = True
            else:
                str += c
        prev_char = c

        if (len(elements) == 6):
            complete_data.append(elements)
            elements = []

    elements.append(str)
    complete_data.append(elements)

    return complete_data


elastic_to_index = "http://" + HOST_TO + ":9200/" + INDEX_TO
settings = {
    "analysis": {
        "analyzer": {
            "termCaseIns": {
                "type": "custom",
                "tokenizer": "keyword",
                "filter": "lowercase"
            }
        }
    }
}
mappings = {
    "properties": {
        "title": {
            "type": "string",
            "fields": {
                "term": {
                    "type": "string",
                    "analyzer": "termCaseIns"
                },
                "orig": {
                    "type": "string",
                    "index": "not_analyzed"
                }
            }
        },
        "language": {
            "type": "string",
            "fields": {
                "term": {
                    "type": "string",
                    "analyzer": "termCaseIns"
                },
                "orig": {
                    "type": "string",
                    "index": "not_analyzed"
                }
            }
        },
        "metadata": {
            "properties": {
                "level": {
                    "type": "string",
                    "analyzer": "termCaseIns"
                }
            }
        },
        "status": {
            "type": "string",
            "analyzer": "termCaseIns"
        },
        "statusShort": {
            "type": "string",
            "analyzer": "termCaseIns"
        }
    }
}

res = requests.get(elastic_to_index)
if res:
    if RECREATE_INDEX:
        res = requests.delete(elastic_to_index)
        print("Index deleted")
        create_index_response = requests.put(elastic_to_index)
        print(create_index_response.text)
else:
    create_index_response = requests.put(elastic_to_index)
    print(create_index_response.text)

time.sleep(5)

print(requests.post(elastic_to_index + "/_close").text)
print("Settings")
print(requests.put(elastic_to_index + "/_settings", data=json.dumps(settings)).text)
print("Mappings")
print(requests.post(elastic_to_index + "/_mapping/" + TYPE, data=json.dumps(mappings)).text)
print(requests.post(elastic_to_index + "/_open").text)

with open(FILE_PATH) as f:
    content = f.readlines()

    icon_dict = {}

    for line in content:
        if line.startswith("INSERT INTO `language` VALUES ("):
            line = line.replace("INSERT INTO `language` VALUES (", "")
            dataset = line.split("),(")
            size = len(dataset)
            for i in range(size):
                data = dataset[i]
                if (i == size - 1):
                    data = data.replace(");", "")
                data = get_elements(data)
                icon_dict[data[1]] = data[2]

    count = 0
    for line in content:
        if line.startswith("INSERT INTO `submissions` VALUES ("):
            line = line.replace("INSERT INTO `submissions` VALUES (", "")
            line = line[:-3] # Removing ');'
            complete_data = get_complete_data(line)
            for data in complete_data:
                statusShort = data[4]
                if 'runtime error' in statusShort.lower():
                    statusShort = 'runtime error'
                if 'memory limit exceeded' in statusShort.lower():
                    statusShort = 'memory limit exceeded'
                if 'wrong answer' in statusShort.lower():
                    statusShort = 'wrong answer'
                if 'time limit exceeded' in statusShort.lower():
                    statusShort = 'time limit exceeded'

                datadict = {
                    "id": data[0],
                    "title": data[1],
                    "metadata": json.loads(data[2].replace('\\', '')),
                    "source": data[3],
                    "status": data[4],
                    "language": data[5],
                    "icon": icon_dict[data[5]],
                    "statusShort": statusShort
                }

                if not json.loads(requests.post('{}/{}/{}'.format(elastic_to_index, TYPE, datadict['id']),
                                                data=json.dumps(datadict)).text)['created']:
                    break
                count += 1
                if (count % 100 == 0):
                    print (count)