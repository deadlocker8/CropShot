package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class SaveFile
{
	public static ResourceBundle bundle = ResourceBundle.getBundle("application/", Locale.GERMANY);	
	
	public static boolean saveSettings(String[] settings)
	{
		File file = new File(System.getenv("APPDATA") + "/Deadlocker/" + bundle.getString("app.name") + "/config.txt");
		if(file.exists())
		{
			try
			{
				BufferedWriter out = new BufferedWriter(new FileWriter(System.getenv("APPDATA") + "/Deadlocker/" + bundle.getString("app.name") + "/config.txt"));
				
				for(int i = 0; i < settings.length; i++)
				{
					out.write(settings[i]);
					out.newLine();
				}				
				out.close();
				
				return true;
			}
			catch(IOException e)
			{
				return false;
			}
		}	
		else
		{				
			try
			{					
				new File(System.getenv("APPDATA") + "/Deadlocker/" + bundle.getString("app.name")).mkdir();				
				
				file.createNewFile();
				BufferedWriter out = new BufferedWriter(new FileWriter(System.getenv("APPDATA") + "/Deadlocker/" + bundle.getString("app.name") + "/config.txt"));
				
				for(int i = 0; i < settings.length; i++)
				{
					out.write(settings[i]);
					out.newLine();
				}						
				out.close();
				
				return true;
			}
			catch(Exception e)
			{	
				e.printStackTrace();
				return false;
			}
		}
	}
}