import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.SetupThreadGroup;
import org.apache.jmeter.util.JMeterUtils;

public class SpringBootLoadTest {

    public static void main(String[] args) throws Exception {
        // Definir o diretório base do JMeter
        String jmeterHome = "/caminho/para/o/diretorio/do/JMeter";

        // Definir as configurações do JMeter
        JMeterUtils.setJMeterHome(jmeterHome);
        JMeterUtils.loadJMeterProperties(JMeterUtils.getJMeterHome() + "/bin/jmeter.properties");
        JMeterUtils.initLocale();

        // Criar um plano de teste
        TestPlan testPlan = new TestPlan("Spring Boot Load Test");

        // Criar um Loop Controller para controlar o número de repetições
        LoopController loopController = new LoopController();
        loopController.setLoops(1);
        loopController.setFirst(true);

        // Criar um grupo de threads para configurar a carga de usuários
        SetupThreadGroup threadGroup = new SetupThreadGroup();
        threadGroup.setNumThreads(10); // Número de threads simulando usuários
        threadGroup.setRampUp(2); // Tempo para atingir o número máximo de threads

        // Criar um HTTP Sampler para enviar requisições HTTP para a aplicação Spring Boot
        HTTPSampler httpSampler = new HTTPSampler();
        httpSampler.setDomain("localhost"); // Altere para o domínio da sua aplicação
        httpSampler.setPort(8080); // Altere para a porta da sua aplicação
        httpSampler.setPath("/endpoint"); // Altere para o endpoint que deseja testar
        httpSampler.setMethod("GET");

        // Adicionar o HTTP Sampler ao Loop Controller
        loopController.addTestElement(httpSampler);
        loopController.setFirst(true);
        loopController.initialize();

        // Configurar o plano de teste
        testPlan.addThreadGroup(threadGroup);
        testPlan.addTestElement(loopController);
        testPlan.setEnabled(true);

        // Configurar as propriedades do JMeter
        JMeterUtils.setProperty("jmeter.save.saveservice.output_format", "xml");
        JMeterUtils.setProperty("jmeter.save.saveservice.response_data", "true");
        JMeterUtils.setProperty("jmeter.save.saveservice.samplerData", "true");

        // Iniciar o motor do JMeter
        StandardJMeterEngine jmeterEngine = new StandardJMeterEngine();
        jmeterEngine.configure(testPlan);
        jmeterEngine.run();
    }
}
