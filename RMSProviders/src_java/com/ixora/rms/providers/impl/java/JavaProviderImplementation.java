/*
 * Created on 17-Mar-2005
 */
package com.ixora.rms.providers.impl.java;

/**
 * Interface that must be implemented by classes that are used with
 * the java provider. Note that the environment is thread safe but
 * the methods will be invoked on different threads.
 * @author Daniel Moraru
 */
public interface JavaProviderImplementation {
	/**
	 * @param parameters parameters required by this instance; they are in the same order
	 * as they were defined in the provider configuration.
	 * @param context the execution context (@see com.ixora.rms.providers.impl.java.JavaProviderImplementationContext)
	 */
	void initialize(String[] parameters, JavaProviderImplementationContext context) throws Exception;
	/**
	 * @return the table with values; check the documentation for providers and their associated
	 * parsers for how the data should be organized in the table<br>
	 * Here are a couple of examples:
	 * <p><b>Example 1</b><br>
	 * <pre>
	 * entity1 counter1 counter2 ... counterN
	 * entity2 counter1 counter2 ... counterN
	 * ...
	 * entityM counter1 counter2 ... counterN
	 * </pre>
	 * This type of table used with a 'Table' parser will populate a tree fragment like the following
	 * where all children entities of MyEntity are similar (that is they have the same set of counters)
	 * <pre>
	 * MyEntity
	 * |_Entity1 with counters counter1,counter2,...counterN
	 * |_Entity2 with counters counter1,counter2,...counterN
	 * |_...
	 * |_EntityN with counters counter1,counter2,...counterN
	 * </pre>
	 * </p>
	 * <p><b>Example 2</b><br>
	 * <pre>
	 * counter1 counter2 ... counterN
	 * counter1 counter2 ... counterN
	 * ...
	 * counter1 counter2 ... counterN
	 * </pre>
	 * This type of table used with a 'Table' parser will populate a tree fragment like the following
	 * where entities are dissimilar (that is they have a different set of counters)
	 * <pre>
	 * MyEntity (or root)
	 * |_Entity1
	 * |_Entity2
	 * |_...
	 * </pre>
	 * </p>
	 * <p><b>Example 3</b><br>
	 * <pre>
	 * property1 value1
	 * property2 value2
	 * ...
	 * propertyN valueN
	 * </pre>
	 * This type of table must be used with a 'Property' parser which produces a tree fragment similar
	 * to the one in Example 3 where entities are dissimilar.
	 * <pre>
	 * MyEntity (or root)
	 * |_Entity1
	 * |_Entity2
	 * |_...
	 * </pre>
	 */
	String[][] getValues() throws Exception;
	/**
	 * Cleanup method. This method will be invoked when the provider is disposed of.
	 * @throws Exception
	 */
	void cleanup() throws Exception;
}
