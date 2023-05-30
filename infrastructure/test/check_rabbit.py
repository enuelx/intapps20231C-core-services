from websocket import create_connection
import webstompy


class MyListener(webstompy.StompListener):
    def on_message(self, frame):
        print('Listener caught this message: ', frame.message)


host = '15.229.178.29'
port = '8080'
endpoint = '/users'
url = 'ws://{0}:{1}{2}'.format(host, port, endpoint)

print("WebSocket URL: ", url)

try:
    ws_echo = create_connection(url)
    connection = webstompy.StompConnection(connector=ws_echo)
    connection.add_listener(MyListener())
    connection.connect(timeout=2000)
    connection.subscribe(destination='/topic/trading', id='0')
    connection.send(destination='/app/send/trading',
                    message='hello from python')
    data = input("Enter for Exit")
    ws_echo.close()
except Exception as error:
    print("Error: ", error)
