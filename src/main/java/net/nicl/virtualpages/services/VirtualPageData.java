package net.nicl.virtualpages.services;

/**
 * This service offer some information required to serve virtual pages
 * @author Niclas Meier
 *
 */
public interface VirtualPageData
{
	/**
	 * Is the page a virtual one
	 * @param pageName The page name
	 * @return <code>true</code> if the page is the currently served virtual page
	 */
	public boolean isVirtualPage(String pageName);

	/**
	 * Is the page a virtual one
	 * @param className The page class name
	 * @return <code>true</code> if the page is the currently served virtual page
	 */
	public boolean isVirtualPageClass(String className);

	/**
	 * The page name of the currently processed virtual page
	 * @return The (logical) page name
	 */
	public String getPageName();

	/**
	 * The class name of the virtual pages
	 * @return The virtual class name of the virtual page
	 */
	public String getVirtualPageClassName();

	public void init(boolean deliveringVirtualPage, String pageName, String virtualPageClassName);
}
