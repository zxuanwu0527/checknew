import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class PrepareData {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String fileName="E:"+File.separator+"data.csv";
        File f=new File(fileName);
        OutputStream out =new FileOutputStream(f,true);
        StringBuilder strBf = new StringBuilder(50000);
        //String str="\r\nRollen";  ø…“‘ªª––
        int count = 0;
        for(; count < 1000000; count++){
        	strBf.append(count+",OPERATIONS"+count+",BOSTON"+count+"\r\n");
        	if(count % 1000 == 0){
        		flushBuffer(strBf, out);
        	}
        }
        flushBuffer(strBf, out);
        out.close();
	}

	public static void flushBuffer( StringBuilder strBf, OutputStream out ) throws IOException{
		byte[] b=strBf.toString().getBytes();
		for (int len = 0; len < b.length; len++) {
			out.write(b[len]);
		}
		out.flush();
		strBf.delete(0, strBf.length());
	}
}
