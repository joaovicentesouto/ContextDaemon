import os, subprocess, sys, time, json

current_milli_time = lambda: int(round(time.time() * 1000))

data = {
        'version': 1.1, 
        'confidence': 0, 
        't': current_milli_time(), 
        'unit': 2224179556, 
        'error': 0, 
        'dev': 0, 
        'y': 302, 
        'x': 302, 
        'z': 0, 
        'value': 30,
        'workflow': 101
    }

print("### Begin script ###\n")
print("Calling workflow 101 with: ", json.dumps(data))
subprocess.check_call(['python3', 'wf101', json.dumps(data)])
print("\n### Begin script ###\n")