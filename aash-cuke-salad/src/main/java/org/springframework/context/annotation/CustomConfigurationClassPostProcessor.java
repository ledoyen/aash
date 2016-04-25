// package org.springframework.context.annotation;
//
// import static org.springframework.context.annotation.AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR;
// import static org.springframework.context.annotation.ConfigurationClassPostProcessor.IMPORT_REGISTRY_BEAN_NAME;
//
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.Comparator;
// import java.util.HashSet;
// import java.util.LinkedHashSet;
// import java.util.List;
// import java.util.Set;
//
// import org.apache.commons.logging.Log;
// import org.apache.commons.logging.LogFactory;
// import org.springframework.beans.factory.config.BeanDefinition;
// import org.springframework.beans.factory.config.BeanDefinitionHolder;
// import org.springframework.beans.factory.config.SingletonBeanRegistry;
// import org.springframework.beans.factory.support.BeanDefinitionRegistry;
// import org.springframework.beans.factory.support.BeanNameGenerator;
// import org.springframework.context.annotation.ConfigurationClassPostProcessor;
// import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
//
// public class CustomConfigurationClassPostProcessor extends ConfigurationClassPostProcessor {
//
// private final Log logger = LogFactory.getLog(getClass());
//
// public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
// List<BeanDefinitionHolder> configCandidates = new ArrayList<BeanDefinitionHolder>();
// String[] candidateNames = registry.getBeanDefinitionNames();
//
// for (String beanName : candidateNames) {
// BeanDefinition beanDef = registry.getBeanDefinition(beanName);
// if (ConfigurationClassUtils.isFullConfigurationClass(beanDef)
// || ConfigurationClassUtils.isLiteConfigurationClass(beanDef)) {
// if (logger.isDebugEnabled()) {
// logger.debug("Bean definition has already been processed as a configuration class: " + beanDef);
// }
// } else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) {
// configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
// }
// }
//
// // Return immediately if no @Configuration classes were found
// if (configCandidates.isEmpty()) {
// return;
// }
//
// // Sort by previously determined @Order value, if applicable
// Collections.sort(configCandidates, new Comparator<BeanDefinitionHolder>() {
// @Override
// public int compare(BeanDefinitionHolder bd1, BeanDefinitionHolder bd2) {
// int i1 = ConfigurationClassUtils.getOrder(bd1.getBeanDefinition());
// int i2 = ConfigurationClassUtils.getOrder(bd2.getBeanDefinition());
// return (i1 < i2) ? -1 : (i1 > i2) ? 1 : 0;
// }
// });
//
// // Detect any custom bean name generation strategy supplied through the enclosing application context
// SingletonBeanRegistry singletonRegistry = null;
// if (registry instanceof SingletonBeanRegistry) {
// singletonRegistry = (SingletonBeanRegistry) registry;
// if (!this.localBeanNameGeneratorSet
// && singletonRegistry.containsSingleton(CONFIGURATION_BEAN_NAME_GENERATOR)) {
// BeanNameGenerator generator = (BeanNameGenerator) singletonRegistry
// .getSingleton(CONFIGURATION_BEAN_NAME_GENERATOR);
// this.componentScanBeanNameGenerator = generator;
// this.importBeanNameGenerator = generator;
// }
// }
//
// // Parse each @Configuration class
// ConfigurationClassParser parser = new ConfigurationClassParser(this.metadataReaderFactory, this.problemReporter,
// this.environment, this.resourceLoader, this.componentScanBeanNameGenerator, registry);
//
// Set<BeanDefinitionHolder> candidates = new LinkedHashSet<BeanDefinitionHolder>(configCandidates);
// Set<ConfigurationClass> alreadyParsed = new HashSet<ConfigurationClass>(configCandidates.size());
// do {
// parser.parse(candidates);
// parser.validate();
//
// Set<ConfigurationClass> configClasses = new LinkedHashSet<ConfigurationClass>(
// parser.getConfigurationClasses());
// configClasses.removeAll(alreadyParsed);
//
// // Read the model and create bean definitions based on its content
// if (this.reader == null) {
// this.reader = new ConfigurationClassBeanDefinitionReader(registry, this.sourceExtractor,
// this.resourceLoader, this.environment, this.importBeanNameGenerator,
// parser.getImportRegistry());
// }
// this.reader.loadBeanDefinitions(configClasses);
// alreadyParsed.addAll(configClasses);
//
// candidates.clear();
// if (registry.getBeanDefinitionCount() > candidateNames.length) {
// String[] newCandidateNames = registry.getBeanDefinitionNames();
// Set<String> oldCandidateNames = new HashSet<String>(Arrays.asList(candidateNames));
// Set<String> alreadyParsedClasses = new HashSet<String>();
// for (ConfigurationClass configurationClass : alreadyParsed) {
// alreadyParsedClasses.add(configurationClass.getMetadata().getClassName());
// }
// for (String candidateName : newCandidateNames) {
// if (!oldCandidateNames.contains(candidateName)) {
// BeanDefinition beanDef = registry.getBeanDefinition(candidateName);
// if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef,
// this.metadataReaderFactory)
// && !alreadyParsedClasses.contains(beanDef.getBeanClassName())) {
// candidates.add(new BeanDefinitionHolder(beanDef, candidateName));
// }
// }
// }
// candidateNames = newCandidateNames;
// }
// } while (!candidates.isEmpty());
//
// // Register the ImportRegistry as a bean in order to support ImportAware @Configuration classes
// if (singletonRegistry != null) {
// if (!singletonRegistry.containsSingleton(IMPORT_REGISTRY_BEAN_NAME)) {
// singletonRegistry.registerSingleton(IMPORT_REGISTRY_BEAN_NAME, parser.getImportRegistry());
// }
// }
//
// if (this.metadataReaderFactory instanceof CachingMetadataReaderFactory) {
// ((CachingMetadataReaderFactory) this.metadataReaderFactory).clearCache();
// }
// }
// }
