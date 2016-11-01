package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.ResourceBundle;

public class ReadFile 
{
	public static ResourceBundle bundle = ResourceBundle.getBundle("application/", Locale.GERMANY);
	
	public static String[] readSettings()
	{	
		
		String[] settings = new String[5];
		File file = new File(System.getenv("APPDATA") + "/Deadlocker/" + bundle.getString("app.name") + "/config.txt");
		if(file.exists())
		{
			try
			{
				FileInputStream fis = new FileInputStream(System.getenv("APPDATA") + "/Deadlocker/" + bundle.getString("app.name") + "/config.txt");
				BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			
				settings[0] = reader.readLine();	
				settings[1] = reader.readLine();
				settings[2] = reader.readLine();	
				settings[3] = reader.readLine();	
				settings[4] = reader.readLine();	
				reader.close();		
				
				return settings;				
			}
			catch(IOException e)
			{
				return null;
			}
		}
		else
		{
			String[] s = new String[5];
			s[0] = "0";
			s[1] = "0";
			s[2] = "10";
			s[3] = "10";
			s[4] = "null";			
			
			if(SaveFile.saveSettings(s))
			{				
				return s;
			}
			else
			{
				return null;
			}					
		}
	}	
}