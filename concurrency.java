class concurrency{

boolean bloquejat;

	public void concurrency()
	{
		bloquejat=false;
		
	}
	
	public void lock()
	{
		bloquejat=true;	
	}
	
	public void unLock()
	{
		bloquejat=false;	
	}
	
	public boolean isLocked()
	{
		if(bloquejat==true)
			return true;
		else
			return false;	
	}




}
