package cn.atd3.test;

public class Attachment {
	IdName attachment;
	int id;
	String name;
	String type;
	
	public Attachment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Attachment [attachment=" + attachment + ", id=" + id + ", name=" + name + ", type=" + type + "]";
	}

	public IdName getAttachment() {
		return attachment;
	}

	public void setAttachment(IdName attachment) {
		this.attachment = attachment;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
