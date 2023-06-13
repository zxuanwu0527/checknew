package test.imp.date;

public interface DataLoadConstants{
	public enum InsertTye {
		NORMAL, DIRECT, EXTERNAL;
	}
	public enum ParamName{
		ROWS("rows"),
		BINDSIZE("bindsize"),
		READSIZE("readsize"),
		STREAMSIZE("streamsize"),
		DATE_CACHE("date_cache"),
		THREADS("threads");
		
		private final String name;

		private ParamName(String name) {
			this.name = name;
		}
		
		public String getName(){
			return this.name;
		}
	}
}

