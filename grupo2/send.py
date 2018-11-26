import os, subprocess, sys, time, json

current_milli_time = lambda: int(round(time.time() * 1000))
var = 0

temp = {
        'version': 1.1, 
        'confidence': 0, 
        't': 0, 
        'unit': 2224179556, 
        'error': 0, 
        'dev': 0, 
        'y': 0, 
        'x': 0, 
        'z': 0, 
        'value': 26,
        'mac' : 1
    }
hum = {
        'version': 1.1, 
        'confidence': 0, 
        't': 0, 
        'unit': 2224179500, 
        'error': 0, 
        'dev': 0, 
        'y': 0, 
        'x': 0, 
        'z': 0, 
        'value': 26,
        'mac' : 1
    }

temp['x'] = 300
temp['y'] = 300
temp['t'] = current_milli_time()
subprocess.check_call(['python3', 'wf103', json.dumps(temp)])

for i in range(500000):
    print("Send %d and t=%d", i, current_milli_time())
    var = var + 1
    temp['x'] = 302
    temp['y'] = 302
    temp['value'] = 28 + (var % 3)
    temp['t'] = current_milli_time()
    subprocess.check_call(['python3', 'wf100', json.dumps(temp)])
    
    var = var + 1
    temp['x'] = 298
    temp['y'] = 298
    temp['value'] = 28 + (var % 3)
    temp['t'] = current_milli_time()
    subprocess.check_call(['python3', 'wf100', json.dumps(temp)])
    
    var = var + 1
    hum['x'] = 302
    hum['y'] = 302
    hum['value'] = 60 + (var % 3)
    hum['t'] = current_milli_time()
    subprocess.check_call(['python3', 'wf100', json.dumps(hum)])
    
    var = var + 1
    hum['x'] = 298
    hum['y'] = 298
    hum['value'] = 60 + (var % 3)
    hum['t'] = current_milli_time()
    subprocess.check_call(['python3', 'wf100', json.dumps(hum)])
    time.sleep(1)