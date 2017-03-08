package endeavor85.billsorter;

class PatternSetting
{
	protected String	label;
	protected String	idPattern;
	protected String	dateMatcher;
	protected String	parsedDateFormat;
	protected String	filenamePrefix;
	protected String	dateFormat	= "yyyy_MM_dd";
	protected String	filenameSuffix;
	protected String	destinationFolder;

	public PatternSetting()
	{}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getIdPattern()
	{
		return idPattern;
	}

	public void setIdPattern(String idPattern)
	{
		this.idPattern = idPattern;
	}

	public String getDateMatcher()
	{
		return dateMatcher;
	}

	public void setDateMatcher(String dateMatcher)
	{
		this.dateMatcher = dateMatcher;
	}

	public String getParsedDateFormat()
	{
		return parsedDateFormat;
	}

	public void setParsedDateFormat(String parsedDateFormat)
	{
		this.parsedDateFormat = parsedDateFormat;
	}

	public String getFilenamePrefix()
	{
		return filenamePrefix;
	}

	public void setFilenamePrefix(String filenamePrefix)
	{
		this.filenamePrefix = filenamePrefix;
	}

	public String getDateFormat()
	{
		return dateFormat;
	}

	public void setDateFormat(String dateFormat)
	{
		this.dateFormat = dateFormat;
	}

	public String getFilenameSuffix()
	{
		return filenameSuffix;
	}

	public void setFilenameSuffix(String filenameSuffix)
	{
		this.filenameSuffix = filenameSuffix;
	}

	public String getDestinationFolder()
	{
		return destinationFolder;
	}

	public void setDestinationFolder(String destinationFolder)
	{
		this.destinationFolder = destinationFolder;
	}
}
