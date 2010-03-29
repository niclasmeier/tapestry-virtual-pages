package net.nicl.virtualpages.services;

import java.util.List;
import java.util.Set;

import org.apache.tapestry5.internal.services.PageTemplateLocator;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.model.ComponentModel;
import org.apache.tapestry5.model.EmbeddedComponentModel;
import org.apache.tapestry5.model.ParameterModel;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.slf4j.Logger;


/**
 * This is the core class for serving virtual pages. It decorates the original <code>ComponentClassResolver</code> and
 * addes detection of virtual page classes
 * 
 * @author Niclas Meier
 */
public class DecoratedComponentClassResolver implements ComponentClassResolver
{

	/** The original resolver to delegate to */
	private final ComponentClassResolver delegate;

	/** A logger */
	private final Logger logger;

	/** The service lookup templates */
	private final PageTemplateLocator templateLocator;

	/** Current thread locale */
	private final ThreadLocale threadLocale;

	/** Service to store some virtual page data */
	private final VirtualPageData virtualPageData;

	/**
	 * Default constructor
	 * 
	 * @param decorated The decorated service
	 * @param logger The logger
	 * @param templateLocator The template locator
	 * @param threadLocale The locale of the current thread/request
	 * @param virtualPageData The virtual page data
	 */
	DecoratedComponentClassResolver(ComponentClassResolver decorated, Logger logger,
		PageTemplateLocator templateLocator, ThreadLocale threadLocale, VirtualPageData virtualPageData)
		{
		this.delegate = decorated;
		this.logger = logger;
		this.templateLocator = templateLocator;
		this.threadLocale = threadLocale;
		this.virtualPageData = virtualPageData;
		}

	@Override
	public String canonicalizePageName(String pageName)
	{
		if (!delegate.isPageName(pageName))
		{
			// if this is not a standard tapestry page, do not try to perform canonicalization
			return pageName;
		}
		else
		{
			// delegate otherwise
			return delegate.canonicalizePageName(pageName);
		}
	}

	@Override
	public List<String> getPageNames()
	{
		// just delegate
		return delegate.getPageNames();
	}

	@Override
	public boolean isPageName(String pageName)
	{
		// check the delegate fist
		boolean isPageName = delegate.isPageName(pageName);
		logger.info("'{}' is a page name: {}", pageName, isPageName);

		if (!isPageName && !"ForceLoadAtStartup".equals(pageName))
		{
			// if we don't have a standard tapestry page check for a template
			final String virtualPageClassName = "net.nicl.virtualpages.pages." + pageName.replace('/', '.');

			// assume we are serving a virtual page. this will be reset later if we don't find a template
			virtualPageData.init(true, pageName, virtualPageClassName);

			// use the locator (std tapestry lookup mechanism) to find the template
			Resource templateResource = templateLocator.findPageTemplateResource(new ComponentModel()
			{
				@Override
				public boolean isRootClass()
				{
					return false;
				}

				@Override
				public boolean isMixinAfter()
				{
					return false;
				}

				@Override
				public boolean handlesEvent(String eventType)
				{
					return false;
				}

				@Override
				public boolean getSupportsInformalParameters()
				{
					return false;
				}

				@Override
				public List<String> getPersistentFieldNames()
				{
					return null;
				}

				@Override
				public ComponentModel getParentModel()
				{
					return null;
				}

				@Override
				public List<String> getParameterNames()
				{
					return null;
				}

				@Override
				public ParameterModel getParameterModel(String parameterName)
				{
					return null;
				}

				@Override
				public List<String> getMixinClassNames()
				{
					return null;
				}

				@Override
				public String getMeta(String key)
				{
					return null;
				}

				@Override
				public Logger getLogger()
				{
					return logger;
				}

				@Override
				@SuppressWarnings("unchecked")
				public Set<Class> getHandledRenderPhases()
				{
					return null;
				}

				@Override
				public String getFieldPersistenceStrategy(String fieldName)
				{
					return null;
				}

				@Override
				public EmbeddedComponentModel getEmbeddedComponentModel(String componentId)
				{
					return null;
				}

				@Override
				public List<String> getEmbeddedComponentIds()
				{
					return null;
				}

				@Override
				public List<String> getDeclaredParameterNames()
				{
					return null;
				}

				@Override
				public String getComponentClassName()
				{
					return virtualPageClassName;
				}

				@Override
				public Resource getBaseResource()
				{
					return null;
				}
			}, threadLocale.getLocale());

			if (templateResource.exists())
			{
				// if we found a template just return and say this is a page name.
				logger.info("'{}' has a template: {}", pageName, templateResource.toURL());

				return true;
			}
		}

		// reset the virtual page data --> no virtual page is served.
		virtualPageData.init(false, pageName, null);

		return isPageName;
	}

	@Override
	public String resolveComponentTypeToClassName(String componentType)
	{
		// just delegate
		return delegate.resolveComponentTypeToClassName(componentType);
	}

	@Override
	public String resolveMixinTypeToClassName(String mixinType)
	{
		// just delegate
		return delegate.resolveMixinTypeToClassName(mixinType);
	}

	@Override
	public String resolvePageClassNameToPageName(String pageClassName)
	{
		if (virtualPageData.isVirtualPageClass(pageClassName))
		{
			// if the class name is the class name of the current virtual page, return the page name
			return virtualPageData.getPageName();
		}
		else
		{
			// or just delegate otherwise
			return delegate.resolvePageClassNameToPageName(pageClassName);
		}
	}

	@Override
	public String resolvePageNameToClassName(String pageName)
	{
		if (virtualPageData.isVirtualPage(pageName))
		{
			// if we try to resolve the class name of the virtual page just return previously computed class name
			return virtualPageData.getVirtualPageClassName();
		}
		else
		{
			// or delegate otherwise
			return delegate.resolvePageNameToClassName(pageName);
		}
	}

}
