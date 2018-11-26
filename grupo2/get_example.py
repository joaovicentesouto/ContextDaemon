import time, requests, json

get_url ='https://iot.ufsc.br/api/get.php'

CLIENT_CERTIFICATE = ['client-16-A7B64D415BD3E98A.pem', 'client-16-A7B64D415BD3E98A.key']

query = {
        'series' : {
            'version' : 1.1,
            'unit'    : 2224179556,
            'x'       : 300,
            'y'       : 300,
            'z'       : 0,
            'r'       : 0,
            't0'      : 0,
            't1'      : 100,
            'dev'     : 0
        },
        'credentials' : {
        	'domain' : 'grupo2'
        }
    }

session = requests.Session()
session.cert = CLIENT_CERTIFICATE
session.headers = {'Content-type' : 'application/json'}
response = session.post(get_url, json.dumps(query))

print("Get [", str(response.status_code), "]", sep='')
if response.status_code == 200:
    print("Get: OK!\n")
    print(response)
    print(json.dumps(response.json(), indent=4, sort_keys=False))
else:
	print("Get: Failed!")
