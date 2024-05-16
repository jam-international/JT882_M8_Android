package communication;

public class VFKItem {
	
	private Integer Index;
	private String Description;
	private boolean Readable;
	private boolean Writable;
	private String DataType;
	
	
	public synchronized Integer getIndex() {
		return Index;
	}


	public synchronized void setIndex(Integer index) {
		Index = index;
	}


	public synchronized String getDescription() {
		return Description;
	}


	public synchronized void setDescription(String description) {
		Description = description;
	}


	public synchronized boolean isReadable() {
		return Readable;
	}


	public synchronized void setReadable(boolean readable) {
		Readable = readable;
	}


	public synchronized boolean isWritable() {
		return Writable;
	}


	public synchronized void setWritable(boolean writable) {
		Writable = writable;
	}


	public synchronized String getDataType() {
		return DataType;
	}


	public synchronized void setDataType(String dataType) {
		DataType = dataType;
	}


	public VFKItem() {
		super();
	}
	
	
	
}
