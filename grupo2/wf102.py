import os, sys, json, errno
from process_verify import process_is_alive

# If not running -> setup daemon
process_is_alive()

if len(sys.argv) != 2:
    exit(-1)

smartdata = json.loads(sys.argv[1]) # maybe sys.argv[1]['smartdata']
message = {
    'type': 5,
    'smartdata' : smartdata
}

try:
    os.mkfifo('.output')
except OSError as oe: 
    if oe.errno != errno.EEXIST:
        raise

with open('.input', 'w') as fifo_in:
    fifo_in.write(json.dumps(message))

with open('.output', 'r') as fifo_out:
    print(fifo_out.read())
    