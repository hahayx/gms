package com.hh.stringmatch;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class ACAutomation {
	final ACTrieTreeNode root = new ACTrieTreeNode(null, 0);
	// state size
	int size;

	public void buildACAutomation(Collection<String> dictionaryWords, Set<Character> omitCharSet) {
		// build trie tree
		for (String badWord : dictionaryWords) {
			addWord(badWord.trim(), omitCharSet);
		}

		LinkedList<ACTrieTreeNode> queue = new LinkedList<ACTrieTreeNode>();
		queue.addLast(root);
		root.fail = root;

		// BFS init fail point
		while (!queue.isEmpty()) {
			ACTrieTreeNode node = queue.pop();
			// process children nodes
			for (Map.Entry<Character, ACTrieTreeNode> entry : node.childrenNodes.entrySet()) {
				ACTrieTreeNode childNode = entry.getValue();

				// search child node's fail node
				// 构造childNode的fail指针
				/**
				 * 构造失败指针的过程概括起来就一句话：设这个节点childNode上的字母为C，沿着他父亲的失败指针走，直到走到一个节点，
				 * 他的儿子中也有字母为C的节点
				 * 。然后把当前节点的失败指针指向那个字母也为C的儿子。如果一直走到了root都没找到，那就把失败指针指向root
				 */
				ACTrieTreeNode point = node;
				while (point != root) {
					ACTrieTreeNode temp = point.fail.getChildNode(childNode.character);
					if (childNode.characterEquals(temp)) {
						childNode.fail = temp;
						break;
					} else {
						point = point.fail;
					}
				}
				if (point == root) {
					childNode.fail = root;
				}
				queue.addLast(childNode);
			}
		}

	}

	private void addWord(String word, Set<Character> omitCharSet) {
		if (StringUtils.isBlank(word))
			return;

		ACTrieTreeNode point = root;
		for (int i = 0; i < word.length(); ++i) {
			char c = Character.toLowerCase(word.charAt(i));
			if (omitCharSet.contains(c)) {
				continue;
			}

			ACTrieTreeNode node = point.getChildNode(c);
			if (node == null) {
				node = new ACTrieTreeNode(c, point.height + 1);
				++size;
				point.addNode(c, node);
			}
			point = node;
		}
		point.isWord = true;// terminal state node
	}

}

class ACTrieTreeNode {
	final Character character;

	// 树高
	int height;

	// 失败指针
	ACTrieTreeNode fail;

	// true表示从root到this可以组成一个词(终结状态)
	boolean isWord;

	// 用hashMap来代替数组
	HashMap<Character, ACTrieTreeNode> childrenNodes = new HashMap<Character, ACTrieTreeNode>(0);
	
	ACTrieTreeNode parentNodes;

	public ACTrieTreeNode(Character character, int height) {
		this.character = character;
		this.height = height;
	}

	public void addNode(Character c, ACTrieTreeNode node) {
		childrenNodes.put(c, node);
		node.parentNodes = this;
	}

	public ACTrieTreeNode getChildNode(Character c) {
		return childrenNodes.get(c);
	}

	public boolean characterEquals(ACTrieTreeNode node) {
		return node == null ? false : character.equals(node.character);
	}

	@Override
	public String toString() {
		return String.format("{Character=%s, isWord=%s, childrenNodes=%s}", character, isWord, childrenNodes);
	}
}
