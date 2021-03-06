package org.wordchain

import scala.annotation.tailrec

object WordProcessor {

  def getTransition(from: String, to: String, usableWords: Set[String]): List[String] = {
    val treeLevels = createTreeLevels(from, to, usableWords)
    val lastLevel  = treeLevels.keys.max
    val targetNode = treeLevels(lastLevel).find(node => node.word == to) match {
      case Some(x) => x
    }
    from +: getTransitionWay(targetNode)
  }

  private def getTransitionWay(targetNode: Node) = {
    @tailrec
    def helper(currentNode: Node, accumulator: List[String]): List[String] =
      if (currentNode.parent.isEmpty) {
        accumulator.reverse
      } else {
        val parent = currentNode.parent match {
          case Some(p) => p
        }
        helper(parent, accumulator :+ currentNode.word)
      }

    helper(targetNode, List.empty[String])
  }

  private def createTreeLevels(from: String, to: String, usableWords: Set[String]) = {
    @tailrec
    def helper(currentNodes: Set[Node], currentLevel: Int, alreadySeenWords: AlreadySeenWords, accumulator: Map[Int, Set[Node]]): Map[Int, Set[Node]] =
      if (accumulator(currentLevel).exists(node => node.word == to)) {
        accumulator
      } else {
        val newLevel       = currentLevel + 1
        val allChildren    = createNextTreeLevel(currentNodes, usableWords, to, alreadySeenWords)
        val words = allChildren.map(item => item.word)
        val seenWords = alreadySeenWords.add(words)
        val newAccumulator = accumulator + (newLevel -> allChildren)
        helper(allChildren, newLevel, seenWords, newAccumulator)
      }

    val root    = Node(None, from)
    val rootSet = Set(root)
    helper(rootSet, 0, AlreadySeenWords(), Map(0 -> rootSet))
  }

  private def createNextTreeLevel(nodes: Set[Node], usableWords: Set[String], to: String, alreadySeenWords: AlreadySeenWords) =
    nodes.flatMap(node => node.createChildren(usableWords, to, alreadySeenWords))
}
