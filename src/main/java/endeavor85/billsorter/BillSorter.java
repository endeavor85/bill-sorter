package endeavor85.billsorter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

public class BillSorter
{
	private static final String		SETTINGS_JSON_FILE	= "./settings.json";

	String							inputFilename		= null;

	private List<PatternSetting>	patterns			= new ArrayList<>();

	public static void main(String[] args)
	{
		new BillSorter().run(args);
	}

	public BillSorter()
	{
		try
		{
			patterns = loadPatternSettings(SETTINGS_JSON_FILE);
		}
		catch(JsonIOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch(JsonSyntaxException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch(FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private static List<PatternSetting> loadPatternSettings(String settingsJsonFilename) throws JsonIOException, JsonSyntaxException, FileNotFoundException
	{
		JsonArray settingsJson = new JsonParser().parse(new FileReader(settingsJsonFilename)).getAsJsonArray();

		List<PatternSetting> patterns = new ArrayList<>();

		Gson gson = new Gson();

		for(JsonElement patternElement : settingsJson)
		{
			PatternSetting patternSetting = gson.fromJson(patternElement, PatternSetting.class);

			patterns.add(patternSetting);
		}

		System.out.println("Loaded " + patterns.size() + " patterns from " + settingsJsonFilename + "\n");

		return patterns;
	}

	public void run(String[] args)
	{

		if(args.length < 1)
		{
			System.err.println("Missing filename arguments.\n" + getUsage());
			System.exit(1);
		}

		boolean verbose = false;
		int argNum = 0;
		
		if(args[0].equalsIgnoreCase("-v"))
		{
			argNum++;
			verbose = true;
		}
		
		for(; argNum < args.length; argNum++)
		{
			inputFilename = args[argNum];

			try
			{
				System.out.println("Reading " + inputFilename);

				PdfReader reader = new PdfReader(inputFilename);
				int n = reader.getNumberOfPages();
				String pdfText = new String();

				PdfReaderContentParser parser = new PdfReaderContentParser(reader);
				for(int i = 1; i <= n; i++)
				{
					TextExtractionStrategy strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
					pdfText += strategy.getResultantText() + "\n";
				}

				reader.close();

				// print the text contents of the PDF file (for debugging purposes only)
				if(verbose) {
					System.out.println(
							"\n\n\n\nDOCUMENT PDF TEXT STARTS ON NEXT LINE\n"
							+ pdfText
							+ "\nDOCUMENT PDF TEXT ENDED ON PREVIOUS LINE\n\n\n\n");
				}

				PatternSetting patternSetting = determinePatternSetting(pdfText);

				if(patternSetting != null)
				{
					Matcher dateMatcher = Pattern.compile(patternSetting.getDateMatcher(), Pattern.DOTALL).matcher(pdfText);
					if(dateMatcher.matches())
					{
						System.out.println("Date matched: " + dateMatcher.group(1) + dateMatcher.group(2) + dateMatcher.group(3));
						Date date = new SimpleDateFormat(patternSetting.getParsedDateFormat(), Locale.ENGLISH).parse(dateMatcher.group(1) + " " + dateMatcher.group(2) + " " + dateMatcher.group(3));

						String dateStr = new SimpleDateFormat(patternSetting.getDateFormat()).format(date);
						String outputFilename = String.format(patternSetting.getFilenamePrefix() + dateStr + patternSetting.getFilenameSuffix());

						File oldFile = new File(inputFilename);
						System.out.println("Original file: " + oldFile.getAbsolutePath());

						String newFilePath = patternSetting.getDestinationFolder() + File.separator + outputFilename;
						System.out.println("Renaming to: " + outputFilename);
						System.out.println("Moving to: " + newFilePath);

						File newFile = new File(newFilePath);

						if(newFile.exists())
						{
							throw new java.io.IOException("File exists: " + newFilePath);
						}

						// rename file
						try
						{
							Files.copy(oldFile.toPath(), newFile.toPath());
							System.out.println("File renamed to: " + newFilePath);
						}
						catch(Exception e)
						{
							System.err.println("Failed to rename file to: " + newFilePath);
							e.printStackTrace();
						}
					}
					else
					{
						System.err.println("ERROR: Date pattern not found in document text: " + patternSetting.getDateMatcher());
					}
				}
				else
				{
					System.err.println("ERROR: Unable to identify file's pattern, skipping: " + inputFilename);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			System.out.println();
		}
	}

	/**
	 * Iterate over PatternSettings to determine which PatternSetting's idPattern String is matched in the given text.
	 * 
	 * @param pdfText
	 * @return
	 */
	private PatternSetting determinePatternSetting(final String pdfText)
	{
		for(PatternSetting patternSetting : patterns)
		{
			System.out.print("Identifying file: " + patternSetting.getLabel() + " ... ");

			Matcher idMatcher = Pattern.compile(patternSetting.getIdPattern(), Pattern.DOTALL).matcher(pdfText);
			if(idMatcher.matches())
			{
				System.out.println("MATCH!");
				return patternSetting;
			}
			else
			{
				System.out.println("no match.");
			}
		}
		return null;
	}

	public String getUsage()
	{
		return this.getClass().getSimpleName() + "[-v] PDF_FILENAME [PDF_FILENAME ...]\n"
				+ "  -v    verbose mode, prints PDF document text to help debug pattern matching issues";
	}
}
