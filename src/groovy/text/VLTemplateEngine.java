package groovy.text;


import groovy.lang.*;
import org.codehaus.groovy.control.CompilationFailedException;

import java.io.IOException;
import java.io.Reader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class VLTemplateEngine extends TemplateEngine {

    private final ClassLoader parentLoader;
    private static AtomicInteger counter = new AtomicInteger();
    private static final boolean reuseClassLoader = Boolean.getBoolean("groovy.GStringTemplateEngine.reuseClassLoader");

    public VLTemplateEngine() {
        this(VLTemplate.class.getClassLoader());
    }

    public VLTemplateEngine(ClassLoader parentLoader) {
        this.parentLoader = parentLoader;
    }

    public Template createTemplate(Reader reader) throws CompilationFailedException, ClassNotFoundException, IOException {
        return new VLTemplate(reader, this.parentLoader);
    }

    private static class VLTemplate implements Template {
        final Closure template;

        VLTemplate(Reader reader, final ClassLoader parentLoader) throws CompilationFailedException, ClassNotFoundException, IOException {
            StringBuilder templateExpressions = new StringBuilder("package groovy.tmp.templates\n import org.ifelse.vl.*\n import org.ifelse.model.*\n  def getTemplate() { return { out -> out << \"\"\"");
            boolean writingString = true;

            while(true) {
                while(true) {
                    int c = reader.read();
                    if (c == -1) {
                        if (writingString) {
                            templateExpressions.append("\"\"\"");
                        }

                        templateExpressions.append("}}");
                        GroovyClassLoader loader = VLTemplateEngine.reuseClassLoader && parentLoader instanceof GroovyClassLoader ? (GroovyClassLoader)parentLoader : (GroovyClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
                            public Object run() {
                                return new GroovyClassLoader(parentLoader);
                            }
                        });

                        Class groovyClass;
                        try {
                            groovyClass = loader.parseClass(new GroovyCodeSource(templateExpressions.toString(), "GStringTemplateScript" + VLTemplateEngine.counter.incrementAndGet() + ".groovy", "x"));
                        } catch (Exception var10) {
                            throw new GroovyRuntimeException("Failed to parse template script (your template may contain an error or be trying to use expressions not currently supported): " + var10.getMessage());
                        }

                        try {
                            GroovyObject script = (GroovyObject)groovyClass.newInstance();
                            this.template = (Closure)script.invokeMethod("getTemplate", (Object)null);
                            this.template.setResolveStrategy(1);
                            return;
                        } catch (InstantiationException var8) {
                            throw new ClassNotFoundException(var8.getMessage());
                        } catch (IllegalAccessException var9) {
                            throw new ClassNotFoundException(var9.getMessage());
                        }
                    }

                    if (c == 60) {
                        c = reader.read();
                        if (c == 37) {
                            c = reader.read();
                            if (c == 61) {
                                parseExpression(reader, writingString, templateExpressions);
                                writingString = true;
                            } else {
                                parseSection(c, reader, writingString, templateExpressions);
                                writingString = false;
                            }
                            continue;
                        }

                        appendCharacter('<', templateExpressions, writingString);
                        writingString = true;
                    } else if (c == 34) {
                        appendCharacter('\\', templateExpressions, writingString);
                        writingString = true;
                    } else if (c == 36) {
                        appendCharacter('$', templateExpressions, writingString);
                        writingString = true;
                        c = reader.read();
                        if (c == 123) {
                            appendCharacter('{', templateExpressions, writingString);
                            writingString = true;
                            this.parseGSstring(reader, writingString, templateExpressions);
                            writingString = true;
                            continue;
                        }
                    }

                    appendCharacter((char)c, templateExpressions, writingString);
                    writingString = true;
                }
            }
        }

        private static void appendCharacter(char c, StringBuilder templateExpressions, boolean writingString) {
            if (!writingString) {
                templateExpressions.append("out << \"\"\"");
            }

            templateExpressions.append(c);
        }

        private void parseGSstring(Reader reader, boolean writingString, StringBuilder templateExpressions) throws IOException {
            if (!writingString) {
                templateExpressions.append("\"\"\"; ");
            }

            int c;
            do {
                c = reader.read();
                if (c == -1) {
                    break;
                }

                templateExpressions.append((char)c);
            } while(c != 125);

        }

        private static void parseSection(int pendingC, Reader reader, boolean writingString, StringBuilder templateExpressions) throws IOException {
            if (writingString) {
                templateExpressions.append("\"\"\"; ");
            }

            templateExpressions.append((char)pendingC);

            while(true) {
                int c = reader.read();
                if (c == -1) {
                    break;
                }

                if (c == 37) {
                    c = reader.read();
                    if (c == 62) {
                        break;
                    }

                    templateExpressions.append('%');
                }

                templateExpressions.append((char)c);
            }

            templateExpressions.append(";\n ");
        }

        private static void parseExpression(Reader reader, boolean writingString, StringBuilder templateExpressions) throws IOException {
            if (!writingString) {
                templateExpressions.append("out << \"\"\"");
            }

            templateExpressions.append("${");

            while(true) {
                int c = reader.read();
                if (c == -1) {
                    break;
                }

                if (c == 37) {
                    c = reader.read();
                    if (c == 62) {
                        break;
                    }

                    templateExpressions.append('%');
                }

                templateExpressions.append((char)c);
            }

            templateExpressions.append('}');
        }

        public Writable make() {
            return this.make((Map)null);
        }

        public Writable make(Map map) {
            Closure template = ((Closure)this.template.clone()).asWritable();
            Binding binding = new Binding(map);
            template.setDelegate(binding);
            return (Writable)template;
        }
    }
}
