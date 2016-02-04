package com.upsoft.thrift.node.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;

import com.upsoft.thrift.node.Node;
import com.upsoft.thrift.node.NodeService;
import com.upsoft.thrift.node.SubNode;
import com.xjkwq1qq.annotation.ThriftService;

@ThriftService("NodeService")
public class NodeServiceImpl implements NodeService.Iface {

	@Override
	public Node getNode(int id) throws TException {
		Node node = new Node();
		node.setId(12);
		node.setName("���Բ˵�");
		List<SubNode> subNodes = new ArrayList<SubNode>();
		subNodes.add(new SubNode(1, "�����Ӳ˵�1", 1));
		subNodes.add(new SubNode(2, "�����Ӳ˵�2", 2));
		node.setSubNodeList(subNodes);
		return node;
	}

}
