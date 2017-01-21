import socket

server = '172.23.181.78'
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((server, 5000))

while True:
    data = s.recv(1024).decode()
    print(data)
    text = input()
    s.send(text.encode())
