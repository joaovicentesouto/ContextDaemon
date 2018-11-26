#!/usr/bin/env python3
import time, requests, json

put_url ='https://iot.lisha.ufsc.br/api/put.php'
attach_url ='https://iot.lisha.ufsc.br/api/attach.php'

CLIENT_CERTIFICATE = ['client-16-A7B64D415BD3E98A.pem', 'client-16-A7B64D415BD3E98A.key']

attach_query = {
    'series': {
        'version': 1.1, 
        't0': 0, 
        't1':18446744073709551605, 
        'unit': 2224179556, 
        'dev': 0, 
        'r':3000, 
        'y': 300, 
        'x': 300,
        'z': 0,
        'workflow': 100
    }, 
    'credentials': {
        'domain': 'grupo2'
    }
}
query = {
    'smartdata': [ {
        'version': 1.1, 
        'confidence': 0, 
        'time': 0, 
        'unit': 2224179556, 
        'error': 0, 
        'dev': 0, 
        'y': 300, 
        'x': 300, 
        'z': 0,
        'value': 100
    } ], 
    'credentials': {
        'domain': 'grupo2'
    }
}

session = requests.Session()
session.cert = CLIENT_CERTIFICATE

session.headers = {'Content-type' : 'application/json'}
response = session.post(attach_url, json.dumps(attach_query))

print("Attach [", str(response.status_code), "]", sep='')

if response.status_code == 204:
    print('Attach: OK!')
else:
	print("Attach: Failed!")

response = session.post(put_url, json.dumps(query))

print("Put [", str(response.status_code), "]", sep='')

if response.status_code == 204:
    print('Put: OK!\n')
    print(json.dumps(query, indent=4, sort_keys=False))
else:
	print("Put: Failed!")
