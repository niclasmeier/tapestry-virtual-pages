package net.nicl.virtualpages.services;

/**
 * Simple implementation of the  <code>VirtualPageData</code> service
 * @author Niclas Meier
 */
public class VirtualPageDataImpl implements VirtualPageData
{
	private boolean deliveringVirtualPage;

	private String pageName;

	private String virtualPageClassName;

	/**
	 * Init method
	 */
	public void init(boolean deliveringVirtualPage, String pageName, String virtualPageClassName)
	{
		this.deliveringVirtualPage = deliveringVirtualPage;
		this.pageName = pageName;
		this.virtualPageClassName = virtualPageClassName;
	}

	@Override
	public boolean isVirtualPageClass(String className)
	{
		return deliveringVirtualPage && virtualPageClassName.equals(className);
	}

	@Override
	public boolean isVirtualPage(String pageName)
	{
		return deliveringVirtualPage && pageName.equals(this.pageName);
	}

	@Override
	public String getVirtualPageClassName()
	{
		return virtualPageClassName;
	}

	@Override
	public String getPageName()
	{
		return pageName;
	}

}
