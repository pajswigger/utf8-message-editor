package burp

import java.awt.BorderLayout
import java.awt.Component
import java.awt.Font
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


class BurpExtender : IBurpExtender {
    override fun registerExtenderCallbacks(callbacks: IBurpExtenderCallbacks) {
        callbacks.setExtensionName("UTF-8 Message Editor")
        callbacks.registerMessageEditorTabFactory(MessageEditorTabFactory())
    }
}


class MessageEditorTabFactory() : IMessageEditorTabFactory {
    override fun createNewInstance(controller: IMessageEditorController?, editable: Boolean): IMessageEditorTab {
        return MessageEditorTab(editable)
    }
}


class MessageEditorTab(editable: Boolean) : IMessageEditorTab {
    private val messageEditorPanel = MessageEditorPanel(editable)

    override val tabCaption = "UTF-8"

    override val uiComponent: Component
        get() = messageEditorPanel

    override val message: ByteArray
        get() = messageEditorPanel.text.toByteArray(Charsets.UTF_8)

    override val selectedData: ByteArray?
        get() = messageEditorPanel.selectedText?.toByteArray(Charsets.UTF_8)

    override fun isEnabled(content: ByteArray, isRequest: Boolean) = true

    override fun setMessage(content: ByteArray?, isRequest: Boolean) {
        if(content == null) {
            return
        }
        messageEditorPanel.text = String(content, Charsets.UTF_8) // TODO: fallback to ISO-8859-1?
    }

    override val isModified
        get() = messageEditorPanel.modified
}


class MessageEditorPanel(editable: Boolean): JPanel(), DocumentListener {
    var modified = false
    private var currentText: String? = null

    private val textArea = JTextArea()
    init {
        textArea.font = Font("Courier New", Font.PLAIN, 13)
        textArea.isEditable = editable
        textArea.document.addDocumentListener(this)
        layout = BorderLayout()
        add(JScrollPane(textArea), BorderLayout.CENTER)
    }

    var text: String
        get() = textArea.text
        set(text) {
            if(currentText == text) {
                return
            }
            currentText = text
            textArea.text = text
            modified = false
        }

    val selectedText: String?
        get() = textArea.selectedText

    override fun changedUpdate(e: DocumentEvent?) {
        modified = true
    }

    override fun insertUpdate(e: DocumentEvent?) {
        modified = true
    }

    override fun removeUpdate(e: DocumentEvent?) {
        modified = true
    }
}
