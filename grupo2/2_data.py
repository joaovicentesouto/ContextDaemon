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
        'value': 26,
        'workflow': 100
    }

print("### Begin script ###\n")
print("Calling workflow 100 with: ", json.dumps(data), "\n")
subprocess.check_call(['python3', 'wf100', json.dumps(data)])
print("\n### End script ###\n")