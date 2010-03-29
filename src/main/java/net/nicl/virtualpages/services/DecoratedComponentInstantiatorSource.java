package net.nicl.virtualpages.services;

import java.util.List;
import java.util.Set;

import org.apache.tapestry5.internal.InternalComponentResources;
import org.apache.tapestry5.internal.services.ComponentInstantiatorSource;
import org.apache.tapestry5.internal.services.Instantiator;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.internal.services.CtClassSource;
import org.apache.tapestry5.ioc.services.ClassFactory;
import org.apache.tapestry5.model.ComponentModel;
import org.apache.tapestry5.model.EmbeddedComponentModel;
import org.apache.tapestry5.model.ParameterModel;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.services.InvalidationEventHub;
import org.slf4j.Logger;


/**
 * This decorated <code>ComponentInstantiatorSource</code> is responsible for swapping the instantiator for the page class
 * with the one of the default class.
 * @author Niclas Meier
 */
public class DecoratedComponentInstantiatorSource implements ComponentInstantiatorSource
{

	/** Decorated <code>ComponentInstantiatorSource</code> */
	private final ComponentInstantiatorSource delegate;

	/** Virtual page data service */
	private final VirtualPageData virtualPageData;

	/**
	 * Default constructor
	 * @param delegate The delegate
	 * @param virtualPageData The virtual page meta data
	 */
	DecoratedComponentInstantiatorSource(ComponentInstantiatorSource delegate, VirtualPageData virtualPageData)
	{
		this.delegate = delegate;
		this.virtualPageData = virtualPageData;
	}

	@Override
	public void addPackage(String packageName)
	{
		// just delegate
		delegate.addPackage(packageName);
	}

	@Override
	public boolean exists(String className)
	{
		// just delegate
		return delegate.exists(className);
	}

	@Override
	public ClassFactory getClassFactory()
	{
		// just delegate
		return delegate.getClassFactory();
	}

	@Override
	public CtClassSource getClassSource()
	{
		// just delegate
		return delegate.getClassSource();
	}

	@Override
	public Instantiator getInstantiator(String classname)
	{

		if (virtualPageData.isVirtualPageClass(classname))
		{
			// if we are dealing the the virtual page class
			Instantiator defaultInstantiator = delegate.getInstantiator("net.nicl.virtualpages.pages.Default");

			// get the Instantiator of the default class and return it decorated
			return new DecoratedInstantiator(defaultInstantiator, new DecoratedComponentModel(defaultInstantiator.getModel(),
				classname));
		}
		else
		{
			// or just delegate otherwise
			return delegate.getInstantiator(classname);
		}
	}

	public InvalidationEventHub getInvalidationEventHub()
	{
		// just delegate
		return delegate.getInvalidationEventHub();
	}

	/**
	 * This decorated <code>ComponentModel</code> just overrides the <code>getComponentClassName()</code> method
	 * to adapt the model of the default class with the page class
	 */
	private final class DecoratedComponentModel implements ComponentModel
	{
		private final ComponentModel delegateComponentModel;

		private final String componentClassName;

		DecoratedComponentModel(ComponentModel delegateComponentModel, String componentClassName)
		{
			this.delegateComponentModel = delegateComponentModel;
			this.componentClassName = componentClassName;
		}

		public Resource getBaseResource()
		{
			return delegateComponentModel.getBaseResource();
		}

		public String getComponentClassName()
		{
			return componentClassName;
		}

		public List<String> getDeclaredParameterNames()
		{
			return delegateComponentModel.getDeclaredParameterNames();
		}

		public List<String> getEmbeddedComponentIds()
		{
			return delegateComponentModel.getEmbeddedComponentIds();
		}

		public EmbeddedComponentModel getEmbeddedComponentModel(String componentId)
		{
			return delegateComponentModel.getEmbeddedComponentModel(componentId);
		}

		public String getFieldPersistenceStrategy(String fieldName)
		{
			return delegateComponentModel.getFieldPersistenceStrategy(fieldName);
		}

		@SuppressWarnings("unchecked")
		public Set<Class> getHandledRenderPhases()
		{
			return delegateComponentModel.getHandledRenderPhases();
		}

		public Logger getLogger()
		{
			return delegateComponentModel.getLogger();
		}

		public String getMeta(String key)
		{
			return delegateComponentModel.getMeta(key);
		}

		public List<String> getMixinClassNames()
		{
			return delegateComponentModel.getMixinClassNames();
		}

		public ParameterModel getParameterModel(String parameterName)
		{
			return delegateComponentModel.getParameterModel(parameterName);
		}

		public List<String> getParameterNames()
		{
			return delegateComponentModel.getParameterNames();
		}

		public ComponentModel getParentModel()
		{
			return delegateComponentModel.getParentModel();
		}

		public List<String> getPersistentFieldNames()
		{
			return delegateComponentModel.getPersistentFieldNames();
		}

		public boolean getSupportsInformalParameters()
		{
			return delegateComponentModel.getSupportsInformalParameters();
		}

		public boolean handlesEvent(String eventType)
		{
			return delegateComponentModel.handlesEvent(eventType);
		}

		public boolean isMixinAfter()
		{
			return delegateComponentModel.isMixinAfter();
		}

		public boolean isRootClass()
		{
			return delegateComponentModel.isRootClass();
		}
	}

	/**
	 * This decortated <code>Instantiator</code> class joins the instantiator of the default page class with the decorated component model
	 */
	private final class DecoratedInstantiator implements Instantiator
	{
		private final Instantiator delegateInstantiator;

		private final DecoratedComponentModel componentModel;

		DecoratedInstantiator(Instantiator delegateInstantiator, DecoratedComponentModel componentModel)
		{
			this.delegateInstantiator = delegateInstantiator;
			this.componentModel = componentModel;
		}

		public ComponentModel getModel()
		{
			return componentModel;
		}

		public Component newInstance(InternalComponentResources resources)
		{
			return delegateInstantiator.newInstance(resources);
		}

	}

}
