package io.github.apollozhu.lottery.prize

import io.github.apollozhu.lottery.tabbedPane
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Font
import java.util.*
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class LotteryPrizeGeneratePanelManagerPanel : JPanel() {
    private val cardLayout = CardLayout()
    private val panelList = ArrayList<LotteryPrizeGeneratePanel>()
    private val panelsContainer = JPanel()
    private var curIndex = 0
    private var nextId = Integer.MIN_VALUE

    private val label = JLabel("奖项设置")
    private val deleteButton = JButton("删除")
    private val previousButton = JButton("上一个")
    private val nextButton = JButton("下一个")
    private val doneButton = JButton("完成")

    init {
        layout = BorderLayout()

        add(label, BorderLayout.NORTH)
        label.font = label.font.deriveFont(Font.BOLD, 35f)

        add(panelsContainer, BorderLayout.CENTER)
        panelsContainer.layout = cardLayout
        addPrize()

        val controls = JPanel()
        add(controls, BorderLayout.SOUTH)
        controls.add(deleteButton)
        deleteButton.addActionListener { removePrize() }
        deleteButton.isEnabled = hasRemovablePrize()

        val button = JButton("添加")
        controls.add(button)
        button.addActionListener { addPrize() }

        controls.add(previousButton)
        previousButton.addActionListener {
            curIndex--
            indexShifted()
        }

        controls.add(nextButton)
        nextButton.addActionListener {
            curIndex++
            indexShifted()
        }

        controls.add(doneButton)
        doneButton.addActionListener {
            tabbedPane.selectedIndex = 0
        }
    }

    private fun indexShifted() {
        if (!hasRemovablePrize()) {
            previousButton.isEnabled = false
            nextButton.isEnabled = false
            deleteButton.isEnabled = false
        } else {
            previousButton.isEnabled = curIndex - 1 >= 0
            nextButton.isEnabled = curIndex + 1 < panelList.size
            deleteButton.isEnabled = true
        }
        cardLayout.show(panelsContainer, panelList[curIndex].identifier)
        val prizeName = panelList[curIndex].prizeName
        label.text = "奖项设置 ${curIndex + 1}/${panelList.size} ${if (prizeName.isBlank()) "" else " - $prizeName"}"
    }

    private fun addPrize() {
        val newPanel = LotteryPrizeGeneratePanel(nextId++.toString() + "")
        panelList.add(curIndex, newPanel)
        panelsContainer.add(newPanel, newPanel.identifier, curIndex)
        indexShifted()
    }

    internal fun hasRemovablePrize() = panelList.size > 1

    private fun removePrize() {
        panelList.removeAt(curIndex)
        panelsContainer.remove(curIndex)
        indexShifted()
    }

    val prizes: Array<LotteryPrizeModel>
        get() = panelList.map { it.model }
                .filter { it != null }
                .toTypedArray() as Array<LotteryPrizeModel>
}
