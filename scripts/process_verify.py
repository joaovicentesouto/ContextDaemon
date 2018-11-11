import subprocess

def process_is_alive():
    try:
        _file = open("../.pid", "r")
        pid = _file.readline()
        _file.close()

        alive = process_exists(pid)
    except IOError:
        alive = False

    if alive:
        print("IT'S ALIVE!!!")
    else:
        print("OH NO!!!")

#Source: https://stackoverflow.com/questions/38056/how-do-you-check-in-linux-with-python-if-a-process-is-still-running
#proc    -> name/id of the process
#id = 0  -> search for pid (default)
#id = 1  -> search for name

def process_exists(proc, id = 0):
    ps = subprocess.Popen("ps -A", shell=True, stdout=subprocess.PIPE)
    output = ps.stdout.read().decode()
    ps.stdout.close()
    ps.wait()

    for line in output.split("\n"):
        if line != "" and line != None:
            fields = line.split()
            pid = fields[0]
            pname = fields[3]

            if(id == 0):
                if(pid == proc):
                    return True
            else:
                if(pname == proc):
                    return True
    
    return False