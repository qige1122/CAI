/*
 * 系统项目名称
 * com.tinker.collect.monitor.remote.southFactory.snmp
 * SnmpFactoryUtil.java
 * 
 * 2016年1月14日-下午8:00:40
 *  2016Cloudyxuq-版权所有(http://blog.csdn.net/cloudyxuq)
 * 
 */
package com.tinker.cai.remote.snmpFactory;

import java.util.List;
import java.util.Map;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TableUtils;

import com.tinker.cai.remote.util.MibConstant;


/**
 * snmp工具类，用于发送包和接收包
 * SnmpFactoryUtil
 * cloudyxuq
 * 2016年1月14日 下午8:00:40
 * @version 1.0.0
 * 
 */
public class SnmpFactoryUtil
{
  
    /**
     * 获取PDU包
     * @param stOID mibs中的设备oid
     * @param iType pdu类型  GET(get snmp varible、GETNEXT snmp Varible、GETBULK snmp data)
     * @return pdu包
     * @since  1.0.0
     */
    private PDU getPDU(String stOID, int iType) {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(stOID)));
        pdu.setType(iType);
        return pdu;
    }
    /**
     * 获取远程目标对象
     * @param stIP 远程对象ip
     * @return  目标对象
     * @since  1.0.0
     */
    private static Target getTarget(String stIP) {
        Address targetAddress = GenericAddress.parse(stIP);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(MibConstant.COMMUNITY));
        target.setAddress(targetAddress);
        target.setVersion(MibConstant.SNMPVERSION);
        target.setTimeout(MibConstant.TIMEOUT);
        target.setRetries(MibConstant.RETRIES);
        return target;
    }
    /**
     * 通过应答pdu获得mib信息（之前绑定的OID的值）
     * @param stIP ip地址
     * @param stPort 端口
     * @param oidMaps oid封装
     * @param iType 类型
     * @return
     */
    public static List getSNMPTable(String stIP, String stPort, Map oidMaps, int iType) {
        Target target = getTarget(stIP + MibConstant.PUDPREFIX + stPort);
        List list =null;
        try {
        	 
        	
        	//[org.snmp4j.util.TableEvent[index=0,vbs=[1.3.6.1.4.1.2021.11.3.0 = 0, null],status=0,exception=null,report=null], org.snmp4j.util.TableEvent[index=768,vbs=[null, 1.3.6.1.2.1.25.3.3.1.2.768 = 1],status=0,exception=null,report=null], org.snmp4j.util.TableEvent[index=769,vbs=[null, 1.3.6.1.2.1.25.3.3.1.2.769 = 1],status=0,exception=null,report=null]]
            DefaultUdpTransportMapping udpTransportMap = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(udpTransportMap);
            snmp.listen();
            PDUFactory pf = new DefaultPDUFactory(iType);
            TableUtils tu = new TableUtils(snmp, pf);
            Object [] arrayoids =  oidMaps.values().toArray();
            OID[] columns = new OID[oidMaps.size()];
            if(null!=arrayoids&&arrayoids.length>0){
            	for(int i =0;i<columns.length;i++){
            		//去除掉末尾.0
            		columns[i] = new VariableBinding(new OID(String.valueOf(arrayoids[i]).replaceAll("(\\.0)*$", ""))).getOid();
            	}
            }
             list = tu.getTable(target, columns, null, null);
            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
