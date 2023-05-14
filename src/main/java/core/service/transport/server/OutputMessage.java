package core.service.transport.server;

public class OutputMessage {

  private String content;
  private String from;

  public OutputMessage(){
    this.content = new String("");
    this.from = new String("");
  }

  public OutputMessage(String content, String from){
    this.content = content;
    this.from = from;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getContent(){
    return content;
  }
  
}
