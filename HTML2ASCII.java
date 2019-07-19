import java.io.*;

public class HTML2ASCII extends FilterInputStream
{
	public HTML2ASCII(InputStream in){
		super(in);
	public int read() throws IOException{
		int b = in.read();
		boolean flag1=false, flag2=false, nocomm=false;
		while (b == 60){
			b=in.read();
			if(b==-1) return b;
			if(b==33){
			  b=in.read();
			  if(b==-1) return b;
			  if(b==45){
				b=in.read();
				if(b==-1) return b;
				if(b==45){
					do{
						b=in.read();
						if(b==-1) return b;
						if(b==45){
							if(flag1) flag2=true;
							else flag1=true;
						} else {
							if(flag1 && flag2 && b==62);
							else {
								flag1=false;
								flag2=false;
							}
						}
					}while((!flag1)||(!flag2)||(b!=62));
				} else nocomm=true;
			  } else nocomm=true;
			} else nocomm=true;

			if(nocomm) {
				while(b!=62){
					b=in.read();
					if(b==-1) return b;
				}
			}
			b=in.read();
		}
		return b;
	}
}
