package net.dorokhov.pony.web.client.util;

public class UserAgentUtils {

	private static Info info = null;

	public static Info getInfo() {

		if (info == null) {
			info = initInfo();
		}

		return info;
	}

	private static native Info initInfo() /*-{

        var parser = new $wnd.UAParser();

        var result = parser.getResult();

        return @net.dorokhov.pony.web.client.util.UserAgentUtils.Info::new(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(
            result.browser.name, result.browser.major, result.browser.version,
			result.device.model, result.device.type, result.device.vendor,
			result.engine.name, result.engine.version,
			result.os.name, result.os.version,
			result.cpu.architecture
        );
    }-*/;

	public static class Info {

		private final String browserName;
		private final String browserMajor;
		private final String browserVersion;

		private final String deviceModel;
		private final String deviceType;
		private final String deviceVendor;

		private final String engineName;
		private final String engineVersion;

		private final String osName;
		private final String osVersion;

		private final String cpu;

		private Info(String aBrowserName, String aBrowserMajor, String aBrowserVersion,
					String aDeviceModel, String aDeviceType, String aDeviceVendor,
					String aEngineName, String aEngineVersion,
					String aOsName, String aOsVersion,
					String aCpu) {
			browserName = aBrowserName;
			browserMajor = aBrowserMajor;
			browserVersion = aBrowserVersion;
			deviceModel = aDeviceModel;
			deviceType = aDeviceType;
			deviceVendor = aDeviceVendor;
			engineName = aEngineName;
			engineVersion = aEngineVersion;
			osName = aOsName;
			osVersion = aOsVersion;
			cpu = aCpu;
		}

		public String getBrowserName() {
			return browserName;
		}

		public String getBrowserMajor() {
			return browserMajor;
		}

		public String getBrowserVersion() {
			return browserVersion;
		}

		public String getDeviceModel() {
			return deviceModel;
		}

		public String getDeviceType() {
			return deviceType;
		}

		public String getDeviceVendor() {
			return deviceVendor;
		}

		public String getEngineName() {
			return engineName;
		}

		public String getEngineVersion() {
			return engineVersion;
		}

		public String getOsName() {
			return osName;
		}

		public String getOsVersion() {
			return osVersion;
		}

		public String getCpu() {
			return cpu;
		}

	}

}
