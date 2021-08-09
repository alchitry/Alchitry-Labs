import java.io.File

import com.alchitry.labs.project.Project

import org.eclipse.swt.widgets.*
import org.eclipse.swt.graphics.Color
import com.alchitry.labs.gui.main.MainWindow
import com.alchitry.labs.gui.Theme


fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Usage: java -XstartOnFirstThread -jar lucid-hdl.jar  <alp-file>")
        return
    }

    // FIXME: ↓ referenced by console-log.
    val display = Display()
    MainWindow.shell = Shell(display)
    Theme.errorTextColor = Color(display, 255, 25, 25)
    Theme.editorForegroundColor = Color(display, 0, 0, 0)
    // FIXME: ↑

    val alp = File(args[0])
    val project = Project.openXML(alp)
    project.build(false)

    println("Done.")
}
