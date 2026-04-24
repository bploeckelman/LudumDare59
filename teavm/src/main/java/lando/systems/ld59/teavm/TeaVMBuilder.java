package lando.systems.ld59.teavm;

import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaCompiler;
import com.github.xpenatan.gdx.teavm.backends.web.config.backend.WebBackend;
import java.io.File;

import lando.systems.ld59.Config;
import org.teavm.tooling.TeaVMSourceFilePolicy;
import org.teavm.tooling.sources.DirectorySourceFileProvider;
import org.teavm.vm.TeaVMOptimizationLevel;

/** Builds the TeaVM/HTML application. */
public class TeaVMBuilder {
    public static void main(String[] args) {
        // Typically set by the Gradle task, but can also be set here or with the command-line arg "debug"
        boolean debug = false;
        // Typically set by the Gradle task, but can also be set here or with the command-line arg "run"
        boolean startJetty = false;
        for (String arg : args) {
            if ("debug".equals(arg)) debug = true;
            else if ("run".equals(arg)) startJetty = true;
        }
        new TeaCompiler(
            new WebBackend()
                .setHtmlWidth(Config.window_width)
                .setHtmlHeight(Config.window_height)
                .setHtmlTitle(Config.window_title)
//                .setWebAssembly(true) // Uncomment this line to use WASM output instead of JavaScript output.
                .setStartJettyAfterBuild(startJetty)
                .setJettyPort(8080)
        )
            .addAssets(new AssetFileHandle("../assets"))
            .setOptimizationLevel(debug ? TeaVMOptimizationLevel.SIMPLE : TeaVMOptimizationLevel.ADVANCED)
            .setMainClass(TeaVMLauncher.class.getName())
            .setObfuscated(!debug)
            .setDebugInformationGenerated(debug)
            .setSourceMapsFileGenerated(debug)
            .setSourceFilePolicy(TeaVMSourceFilePolicy.COPY)
            .addSourceFileProvider(new DirectorySourceFileProvider(new File("../core/src/main/java/")))
            // You can also register any classes or packages that require reflection here:
            //.addReflectionClass("lando.systems.ld59.reflect")
            .addReflectionClass("com.badlogic.ashley.core.Entity")
            .addReflectionClass("com.github.tommyettinger.gdcrux")
            .addReflectionClass("lando.systems.ld59.assets")
            .addReflectionClass("lando.systems.ld59.assets.anims")
            // ---------------------------------------------------------------------------------------------------------
            //
            // NOTE: these are the reflection includes from vis-ui.gwt.xml,
            // doesn't seem to make a difference whether included or not...
            //
            // https://github.com/kotcrab/vis-ui/blob/master/ui/src/main/resources/com/kotcrab/vis/vis-ui.gwt.xml
            //
            .addReflectionClass("com.kotcrab.vis.ui.Sizes")
            .addReflectionClass("com.kotcrab.vis.ui.widget")
            .addReflectionClass("com.kotcrab.vis.ui.widget.spinner")
            .addReflectionClass("com.kotcrab.vis.ui.widget.dialog")
            .addReflectionClass("com.kotcrab.vis.ui.widget.color")
            .addReflectionClass("com.kotcrab.vis.ui.widget.tabbedpane")
            .addReflectionClass("com.kotcrab.vis.ui.widget.toast")
            .addReflectionClass("com.kotcrab.vis.ui.util.form")
            .addReflectionClass("com.kotcrab.vis.ui.util.adapter.SimpleListAdapter")
            // "com.kotcrab.vis.ui.widget.file" // NOTE: this is 'gdx-reflect-exclude' in vis-ui.gwt.xml!
            .addReflectionClass("com.kotcrab.vis.ui.widget.file.FileChooserStyle")
            // ---------------------------------------------------------------------------------------------------------
            // Additional reflection classes/packages required that weren't in vis-ui.gwt.xml:
            .addReflectionClass("com.kotcrab.vis.ui.util.form.SimpleFormValidator$FormValidatorStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.BusyBar$BusyBarStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.LinkLabel$LinkLabelStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.ListViewStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.Menu$MenuStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.MenuBar$MenuBarStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.MenuItem$MenuItemStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.MultiSplitPane$MultiSplitPaneStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.Separator$SeparatorStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.Tooltip$TooltipStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.VisCheckBox$VisCheckBoxStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.VisImageButton$VisImageButtonStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.VisImageTextButton$VisImageTextButtonStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.VisSplitPane$VisSplitPaneStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.VisTextButton$VisTextButtonStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.VisTextField$VisTextFieldStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.color.ColorPickerStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.color.ColorPickerWidgetStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.spinner.Spinner$SpinnerStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane$TabbedPaneStyle")
            .addReflectionClass("com.kotcrab.vis.ui.widget.toast.Toast$ToastStyle")
            // ---------------------------------------------------------------------------------------------------------
            .build(new File("build/dist"));
    }
}