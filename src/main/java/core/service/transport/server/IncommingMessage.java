package core.service.transport.server;

public class IncommingMessage {
  String content;
  String from;

  public IncommingMessage(String content, String from){
    this.content = content;
    this.from = from;
  }

  public String getContent(){
    return content;
  }
}
