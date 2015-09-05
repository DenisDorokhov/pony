package net.dorokhov.pony.core.upgrade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class UpgradeWorkerLookupServiceImpl implements UpgradeWorkerLookupService, ApplicationContextAware {

	private static final String VERSION_SEPARATOR_REGEX = "\\.";
	private static final int VERSION_PARTS_COUNT = 3;
	private static final Comparator<String[]> VERSION_COMPARATOR = new VersionComparator();

	private final Logger log = LoggerFactory.getLogger(getClass());

	private ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext aContext) throws BeansException {
		context = aContext;
	}

	@Override
	public List<UpgradeWorker> lookupUpgradeWorkers(String aPackage, String aVersion) {

		String[] version = stringToVersion(aVersion);

		List<VersionWorker> versionWorkers = findAvailableWorkers(aPackage);

		Collections.sort(versionWorkers);

		List<UpgradeWorker> workersToPerform = new ArrayList<>();
		for (VersionWorker worker : versionWorkers) {
			if (VERSION_COMPARATOR.compare(worker.getVersion(), version) > 0) {

				String workerName = worker.getWorker().getSimpleName();
				String beanName = workerName.substring(0, 1).toLowerCase() + workerName.substring(1);

				workersToPerform.add(context.getBean(beanName, UpgradeWorker.class));
			}
		}

		return workersToPerform;
	}

	private String[] stringToVersion(String aVersion) throws IllegalArgumentException {

		if (aVersion == null) {
			throw new IllegalArgumentException("Version must not be null.");
		}

		String[] version = aVersion.split(VERSION_SEPARATOR_REGEX);
		if (version.length != VERSION_PARTS_COUNT) {
			throw new IllegalArgumentException("Version must consist of " + VERSION_PARTS_COUNT + " parts.");
		}
		for (String part : version) {
			if (!part.matches("[0-9]+")) {
				throw new IllegalArgumentException("Version parts must be numeric.");
			}
		}

		return version;
	}

	private List<VersionWorker> findAvailableWorkers(String aPackage) {

		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

		String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
				ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(aPackage)) + "/*.class";

		Resource[] resourceList = null;
		try {
			resourceList = resourcePatternResolver.getResources(packageSearchPath);
		} catch (IOException e) {
			log.error("Could not fetch package resources.", e);
		}

		List<VersionWorker> result = new ArrayList<>();

		if (resourceList != null) {
			for (Resource resource : resourceList) {
				if (resource.isReadable()) {

					MetadataReader metadataReader = null;
					try {
						metadataReader = metadataReaderFactory.getMetadataReader(resource);
					} catch (IOException e) {
						log.warn("Could not read resource [{}].", resource.toString(), e);
					}

					if (metadataReader != null) {
						VersionWorker versionWorker = classToVersionWorker(metadataReader.getClassMetadata().getClassName());
						if (versionWorker != null) {
							result.add(versionWorker);
						}
					}
				}
			}
		}

		return result;
	}

	private VersionWorker classToVersionWorker(String aClass) {

		Class<?> clazz = null;

		try {
			clazz = Class.forName(aClass);
		} catch (ClassNotFoundException e) {
			log.warn("Class [{}] not found.", aClass, e);
		}

		if (clazz != null && UpgradeWorker.class.isAssignableFrom(clazz)) {

			UpgradeWorker.Version versionAnnotation = clazz.getAnnotation(UpgradeWorker.Version.class);
			if (versionAnnotation != null && versionAnnotation.value() != null) {

				String[] version = null;

				try {
					version = stringToVersion(versionAnnotation.value());
				} catch (IllegalArgumentException e) {
					log.warn("Upgrade worker [{}] version [{}] is invalid.", clazz, versionAnnotation.value());
				}

				if (version != null) {
					return new VersionWorker(clazz, version);
				}

			} else {
				log.warn("Upgrade worker [{}] does not have a version defined.", clazz);
			}
		}

		return null;
	}

	private static class VersionWorker implements Comparable<VersionWorker> {

		private final Class worker;

		private final String[] version;

		public VersionWorker(Class aWorker, String[] aVersion) {
			worker = aWorker;
			version = aVersion;
		}

		public Class getWorker() {
			return worker;
		}

		public String[] getVersion() {
			return version;
		}

		@Override
		@SuppressWarnings("NullableProblems")
		public int compareTo(VersionWorker aWorker) {

			if (aWorker == null) {
				return 1;
			}

			return VERSION_COMPARATOR.compare(getVersion(), aWorker.getVersion());
		}
	}

	// Taken from http://stackoverflow.com/questions/10774914/java-sorting-algorithm-version-as-string
	private static class VersionComparator implements Comparator<String[]> {

		public int compare(String[] version1, String[] version2) {

			int length = version1.length;

			if (version2.length > version1.length) {
				length = version2.length;
			}

			for (int i = 0; i < length; i++) {

				String part1 = null;
				if (i < version1.length) {
					part1 = version1[i];
				}
				Integer number1 = (part1 == null) ? 0 : Integer.parseInt(part1);

				String part2 = null;
				if (i < version2.length) {
					part2 = version2[i];
				}
				Integer number2 = (part2 == null) ? 0 : Integer.parseInt(part2);

				if (number1.compareTo(number2) < 0) {
					return -1;
				} else if (number2.compareTo(number1) < 0) {
					return 1;
				}
			}

			return 0;
		}
	}

}
