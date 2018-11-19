import sys, json
from process_verify import process_is_alive

# If not running -> setup daemon
process_is_alive()

if len(sys.argv) != 2:
    exit(-1)

smartdata = json.loads(sys.argv[1])

message = {
    'type': 3,
    'smartdata' : smartdata
}

print(json.dumps(message))

with open('.input', 'w') as fifo:
    fifo.write(json.dumps(message))