package core.service.transport.server;

public class OutputMessage {

  private String from;
  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  private String content;

  public void setContent(String content) {
    this.content = content;
  }

  public String getContent(){
    return content;
  }
  
}
