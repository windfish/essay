package com.demon.test;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class Dom4jParseXmlTest {

	public static void main(String[] args) throws DocumentException {
		/*String str = "<?xml version=\"1.0\" encoding=\"gbk\" standalone=\"yes\"?><ns3:ExchangeEvent ActionType=\"LOGINK_CN_TRACK_STATUS\" EventID=\"07803240-6b28-4058-b87a-f6c8ebe008ec\" xmlns:ns2=\"http://schemas.egs.org.cn/businessdata\" xmlns=\"http://schema.egs.org.cn/exchange/edf\" xmlns:ns3=\"http://schema.egs.org.cn/exchange/exchangetransport\"><ExchangeDataPackage title=\"2d3e8068-d4e7-4828-8f93-0ed193d5e9b8\" expireTime=\"2016-05-24T10:27:21.187+08:00\" createTime=\"2016-05-24T10:27:21.187+08:00\" transactionID=\"02f94c39-22ee-4926-9b22-9c59b6c2a712\" packageID=\"00d39d36-d18e-4be8-8825-878c294fa5c2\"><ExchangeDataPackageUnit entityDispName=\"0a66a10c-ef66-4a7d-ab43-b5f9d9284624\" entityIDName=\"d7433eb8-e6c7-4f1a-b549-7e8b6e318ff9\" source=\"f9aefe48-3f49-47b2-8e74-5677d782ff31\" unitID=\"e72d6b7b-ad4e-486b-89e1-518b7e98aabf\" sequenceInGroup=\"1\" groupSize=\"1\" groupID=\"ca7b1716-5ac4-40f2-846e-6fa22475b825\"><SimpleData FieldValue=\"sU3nhA7Zxq8qDSZ2U9v2xWiVxrGjCZAJNw4r6xSIew+Rbq2l4ISXhEZxpxgspuTs+y/0T8eblVZHsZyoIyLmsj/LXFs2lN/54u2vPRfY44l16x0ya89uR+gLoKoBFM4/SLC6r4lPP/oYtbzYBZHZCcquhuiHfdwHOFu/O8uWKfEvi1kuHsmm3uDYp0vktahGPdNPIp8ldVYKoCK32bDD6JDPJGBlxMfZRi2R7eyLx/wBhPpnqYkcpuMHGPy+h7/fr27CRV/QV+YV8EpBUFCEgEB0p4Yif9YfO2p8ESeoUiM4393+UFW2MJNVBLB3KbsrbM9Ipz5Yo5uWksAE3x/ky89oYbD3EpCfX+q/ub3wXeVtg4T34WFySIbzu81zuB0sT7CFJqWw/kUB2f05WUxfdXFaFsNsE8lorDyP7cE2FBFJSsMBUcjDX3g1AGH5Y3hbhXL/8Qlq/+8txPBDp9oac/WBPRrjIG3QPg78HwB/HoEhEVpn1sWU3CH6L2oLNZMhQY57lHe8/kpUDFI90iC75xOAD5l1DNQY2iLYi6YyBcXAhfv5zJ2rmTbNBvTRxBd3AVKCio9u3K46H3qGul6ul6llPrDbijnEeoflCGoGTUoPab549+cPWlPPPU+uVmDVW5Aj/H0x6htX8QNjmtIAmGfXBCffvRVnqrxR5mTyVfcmI3kM4LhgcjsdcAoiuK1b+UZdRrM+QowZ2rnZ22xZ83813QCdvnOBl7JRM0vYonl/wjYZYSHl2bxYnUanZc1MTsR33emowevQ1Kp5FZC362MZp568HdrSvaAYv7HvLduZkiOgc9hMsOurX6Neg8Fuj5xOVyLIXrq+yGe/xQMHLFKNCCAE+9ynNI+cejB4MTYYoUuWhybM864tkoxuhyPERiiew1FybNGooZTKFoMp0vE6rMD/fenhcOqcadf02453SkYF6UYyU/0hBc+QoEv9Q4NOAt+pJW/7Ht4sHegnSLJJcf8v/XLax60JfCz+uLslUQI+flSJL8kSrQxGDaKbZP5WQZZGU8D7UxRG6TPXYW13OoudV+5arCrelHIoH4tTl3IlYLAsrNFJ/CaHmVq2SaD4EHZZMFm14Zqqu/2wE8zkDWYqExnrFRQ4VPgI+jjl7SxKB1bI0aQM7sH2i01e8cqqZ1x9grpI1QTe4IqYjC9KVnSFtB5r7PtTPt1TUXYn5QXyALN7AjfvFhdBx2DGOfBGoz18J4V/1PwQdf54C7/gThXp61HdYNurLIgrVboS5/M2TdqB4PKGuUDWeu3VIIk7zWcakV+SvuwfwFITH7p8JNeze3Mg1g26ecLYb9tIHVZ1xAsdqqPvSXinzi7jwqDMfomtzrVEgTcipZ6+vtaeUYg/QC1Dkw072rB0Z1gAQxcnj6Toh0bgc2Bjwn3yH6xRGQJs5KXqa8TF8/nKExnm/zz3WTwQaEmxZsMMbIBOelOMtRRy1APgdXbcY6QasUyrskdmxl3fwOynCImFsLqhb3+IjuWXpHBjOZ9KtsnMYShZeVM0M9ZMtpezeKw/jpgkMq3Jh9v3Wp1AkLotm5ed+112Q97bpsRf3qTxjHJ6MvsLVm/oLuYu68CVaZ4+cP34KwZw1OOZKqZoM+K7sIi2NYKYGh/og6v01HR13IAE5KgzWuHLO+uQrlVfnH3EtlBN5W33HmdmjUFqR4lK3YLSzSby+gzF/egmA6raXgF/ODCQaOTsHbMPBAy1nEi+m/swCzLKEq3wvuAmiugfyXB27ilafGOmEMSW7SkZ705cVVeQOqrAWP7CDeql5AOM22Leiv7l5yt2AX6uYy/6dFfB+encPBG+SB+BO+mptpYFCqpSTUL6KmCKpoAtcCFLarO5izmvEmx0qJBYXzIJOPe77w+LDj1fqYcs4tBvk6y7+gjUUw37P4MXedcS2oP5An0clT/gcqSZ0gFpZmm5FP4u3uYqxbGGk00+1GyElB9dHvA90XYyPg==\" FieldName=\"%e8%88%b9%e8%88%b6%e9%9d%a0%e6%b3%8a%e8%ae%a1%e5%88%92\"/></ExchangeDataPackageUnit></ExchangeDataPackage></ns3:ExchangeEvent>";
		Document d = DocumentHelper.parseText(str);
		Element root = d.getRootElement();
		Element exchangeDataPackage = root.element("ExchangeDataPackage");
		String createTime = exchangeDataPackage.attribute("createTime").getValue();
		System.out.println(createTime);*/
		
		xxx xx = new xxx();
		xx.setAaa("aaa");
		xx.setBbb("bbb");
		
		try {
			ByteArrayOutputStream sb = new ByteArrayOutputStream();
			JAXBContext jc = JAXBContext.newInstance(xxx.class);
			Marshaller m = jc.createMarshaller();
			m.marshal(xx, sb);
			System.out.println(sb.toString());

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
	}

}

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Xxx", propOrder = {
    "aaa",
    "bbb"
})
class xxx {
	@XmlElement(name = "Aaa")
	private String aaa;
	@XmlElement(name = "Bbb")
	private String bbb;
	
	public String getAaa() {
		return aaa;
	}
	public void setAaa(String value) {
		this.aaa = value;
	}
	public String getBbb() {
		return bbb;
	}
	public void setBbb(String value) {
		this.bbb = value;
	}
	
}
