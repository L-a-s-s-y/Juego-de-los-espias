package es.ujaen.ssmmaa.curso2023_24.agentes;


import es.ujaen.ssmmaa.OntoJuegoEspias;

import es.ujaen.ssmmaa.curso2023_24.gui.OrganizadorJFrame;
import es.ujaen.ssmmaa.curso2023_24.gui.Selector;
import es.ujaen.ssmmaa.curso2023_24.utils.GestorSuscripciones;
import es.ujaen.ssmmaa.elementos.*;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ProposeInitiator;
import jade.proto.ProposeResponder;
import jade.proto.SubscriptionResponder;
import jade.proto.SubscriptionResponder.Subscription;
import jade.util.Logger;
import jade.util.leap.List;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import static es.ujaen.ssmmaa.Vocabulario.*;
import static es.ujaen.ssmmaa.Vocabulario.TipoAgenteJugador.ESPIA;
import static es.ujaen.ssmmaa.Vocabulario.TipoAgenteJugador.SEGURIDAD;
import static es.ujaen.ssmmaa.Vocabulario.TipoAgenteOrganizador.*;

/**
 *
 * @author acl00111
 * Esqueleto de agente para la estructura general que deben tener todos los
 * agentes
 */

public class AgenteOrganizador extends Agent {
    //Variables del agente
    private Properties prop = new Properties();
    private String nombreArchivoConfiguracion = "";

    private final Codec codec = new SLCodec();
    private ContentManager manager;
    private Ontology ontology;

    private GestorSuscripciones gestor;
    private AID monitorOrg;

    private List listaAgentes;
    private OrganizarJuego organizarJuego;

    private boolean confirmacionEspia = false;
    private boolean confirmacionSeguridad = false;
    private AgenteJugador espia;
    private AgenteJugador seguridad;
    private Partida partida;

    private ArrayList<String> listaTableros;
    //estructuras de datos para la visualizacion de los tableros
    private HashMap<String, HashMap<String, HashMap<Partida, ArrayList<Objetivo>>>> visualizacionJuegos;
    private ArrayList<Objetivo> distribucionPartida;

    protected static Selector myGUI;

    @Override
    protected void setup() {
        //Inicialización de las variables del agente
        Object[] args = getArguments();
        if (args != null) {
            for (Object arg : args) {
                prop = (Properties) arg;
            }
            System.out.println("Se ha cargado el archivo de configuracion");
        }

        gestor = new GestorSuscripciones();
        visualizacionJuegos = new HashMap<>();
        listaTableros = new ArrayList<>();
        //Configuración del GUI
        inicializarGUI();

        //Registro del agente en las Páginas Amarrillas
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(ORGANIZADOR.name());
        sd.setName(NOMBRE_SERVICIO);
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.getMessage();
        }

        //Registro de la Ontología
        try {
            ontology = OntoJuegoEspias.getInstance();
            manager = getContentManager();
            manager.registerLanguage(codec);
            manager.registerOntology(ontology);
        } catch (BeanOntologyException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Se inicia la ejecución del agente: " + this.getName());
        //Añadir las tareas principales

        MessageTemplate msgTemplatePropuesta = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE),
                MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
        addBehaviour(new TareaOrganizarJuegoParticipante(this, msgTemplatePropuesta));

        MessageTemplate msgTemplateSuscripcion = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE),
                MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE));
        addBehaviour(new TareaSubscripcionOrganizadorParticipante(this, msgTemplateSuscripcion));

        addBehaviour(new TareaRecibirPasosPartida());

    }

    @Override
    protected void takeDown() {
        //Eliminar registro del agente en las Páginas Amarillas

        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.getMessage();
        }

        try {
            finalizarAgentes();
        } catch (FailureException e) {
            throw new RuntimeException(e);
        }

        doWait(1000);

        //Liberación de recursos, incluido el GUI
        myGUI.dispose();

        //stopJADE();  //solo para MicroRuntime
        //Despedida
        System.out.println("Finaliza la ejecución del agente: " + this.getName());
    }

    private void finalizarAgentes() throws FailureException {
        finalizarMonitor();
        finalizarJugadores();
        finalizarTableros();
    }

    private void finalizarTableros() throws FailureException {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(TABLERO.name());
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            for (DFAgentDescription dfAgentDescription : result) {
                //mandar un mensaje de terminasion
                String tablero = dfAgentDescription.getName().getLocalName();
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID(tablero, AID.ISLOCALNAME));
                msg.setConversationId("TERMINAR EJECUCION");
                msg.setContent("TERMINAR EJECUCION");
                send(msg);

            }
        } catch (FIPAException e) {
            System.err.println("No se han encontrado tableros");
            throw new FailureException("No se han encontrado tableros");
        }
    }

    private void finalizarJugadores() throws FailureException {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(ESPIA.name());
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            for (DFAgentDescription dfAgentDescription : result) {
                //mandar un mensaje de terminasion
                String espia = dfAgentDescription.getName().getLocalName();
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID(espia, AID.ISLOCALNAME));
                msg.setConversationId("TERMINAR EJECUCION");
                msg.setContent("TERMINAR EJECUCION");
                send(msg);

            }
        } catch (FIPAException e) {
            System.err.println("No se han encontrado espias");
            throw new FailureException("No se han encontrado espias");
        }

        DFAgentDescription template1 = new DFAgentDescription();
        ServiceDescription sd1 = new ServiceDescription();
        sd1.setType(SEGURIDAD.name());
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template1);
            for (DFAgentDescription dfAgentDescription : result) {
                //mandar un mensaje de terminasion
                String seguridad = dfAgentDescription.getName().getLocalName();
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID(seguridad, AID.ISLOCALNAME));
                msg.setConversationId("TERMINAR EJECUCION");
                msg.setContent("TERMINAR EJECUCION");
                send(msg);

            }
        } catch (FIPAException e) {
            System.err.println("No se han encontrado seguridad");
            throw new FailureException("No se han encontrado seguridad");
        }

    }

    private void finalizarMonitor() throws FailureException {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(MONITOR.name());
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            for (DFAgentDescription dfAgentDescription : result) {
                //mandar un mensaje de terminasion
                String monitor = dfAgentDescription.getName().getLocalName();
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID(monitor, AID.ISLOCALNAME));
                msg.setConversationId("TERMINAR EJECUCION");
                msg.setContent("TERMINAR EJECUCION");
                send(msg);

            }
        } catch (FIPAException e) {
            System.err.println("No se ha encontrado monitor");
            throw new FailureException("No se ha encontrado monitor");
        }
    }

    private void inicializarGUI() {
        myGUI = new Selector(this);
        myGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myGUI.pack();
        myGUI.setVisible(true);
        //myGUI.setSize(1200, 800);
    }

    //Métodos de trabajo del agente
    public class TareaOrganizarJuegoParticipante extends ProposeResponder{
        private Agent organizador;
        public TareaOrganizarJuegoParticipante(Agent a, MessageTemplate mt) {
            super(a, mt);
            organizador = a;
        }

        @Override
        protected ACLMessage prepareResponse(ACLMessage propose) throws NotUnderstoodException, RefuseException {
            ACLMessage respuesta = propose.createReply();
            String ontologia = propose.getOntology();

            if(!ontology.getName().equals(ontologia)){
                throw new NotUnderstoodException("No se ha entendido la Ontología");
            }else{
                try {
                    Action actionOrganizarJuego = (Action) manager.extractContent(propose);
                    organizarJuego = (OrganizarJuego) actionOrganizarJuego.getAction();
                    Juego juego = organizarJuego.getJuego();
                    listaAgentes = organizarJuego.getListaJugadores();

                    if(juego.getModo() != ModoJuego.INDIVIDUAL && juego.getModo() != ModoJuego.TORNEO && juego.getModo() != ModoJuego.EQUIPO){
                        respuesta.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        Justificacion justificacion = new Justificacion(juego.getIdJuego(), Motivacion.TIPO_JUEGO_DESCONOCIDO);
                        manager.fillContent(respuesta, justificacion);
                    }else{
                        respuesta.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

                        es.ujaen.ssmmaa.elementos.AgenteOrganizador org = new es.ujaen.ssmmaa.elementos.AgenteOrganizador(organizador.getLocalName(), organizador.getAID(), ORGANIZADOR);
                        Registro registro = new Registro(juego.getIdJuego(), org);
                        manager.fillContent(respuesta, registro);
                        InfoPartida infoPartida = organizarJuego.getConfiguracion();
                        String idPartida = "Partida-" + UUID.randomUUID();

                        //generar objetivos
                        List bajaprioridad = new jade.util.leap.ArrayList();
                        List normalprioridad = new jade.util.leap.ArrayList();
                        List altaprioridad = new jade.util.leap.ArrayList();
                        List maximaprioridad = new jade.util.leap.ArrayList();
                        ArrayList<Objetivo> distribucion = new ArrayList();
                        for (int i = 0; i < NUM_ZONAS; i++) {
                            Objetivo objetivo = Objetivo.getObjetivo();
                            distribucion.add(objetivo);
                            if ( objetivo.equals(Objetivo.BAJA_PRIORIDAD) ){
                                bajaprioridad.add(objetivo.getValor());
                            } else if (objetivo.equals(Objetivo.PRIORIDAD_NORMAL)) {
                                normalprioridad.add(objetivo.getValor());
                            }else if (objetivo.equals(Objetivo.ALTA_PRIORIDAD)){
                                altaprioridad.add(objetivo.getValor());
                            }else if (objetivo.equals(Objetivo.MAXIMA_PRIORIDAD)){
                                maximaprioridad.add(objetivo.getValor());
                            }
                        }

                        List zonas = new jade.util.leap.ArrayList();
                        zonas.add(0, bajaprioridad.size());
                        zonas.add(1, normalprioridad.size());
                        zonas.add(2, altaprioridad.size());
                        zonas.add(3, maximaprioridad.size());

                        distribucionPartida = distribucion;

                        partida = new Partida(idPartida, organizarJuego.getJuego().getIdJuego(), infoPartida, zonas);

                        //Se guarda la info de la partida y su distribucion en un hash map para su posterior visualizacion
                        HashMap<Partida, ArrayList<Objetivo>> distribucionPartida = new HashMap<>();
                        distribucionPartida.put(partida, distribucion);
                        HashMap<String, HashMap<Partida, ArrayList<Objetivo>>> idPartidaPartida = new HashMap<>();
                        idPartidaPartida.put(idPartida, distribucionPartida);
                        visualizacionJuegos.put(organizarJuego.getJuego().getIdJuego(), idPartidaPartida);

                        myGUI.addJuego(organizarJuego.getJuego().getIdJuego());

                        //proponer el juego a los agentes
                        addBehaviour(new TareaComprobarDisponibilidadJugadores(partida));
                        addBehaviour(new ComprobarAgentesJugador(myAgent, 1000));
                    }

                } catch (Codec.CodecException e) {
                    throw new RuntimeException(e);
                } catch (OntologyException e) {
                    throw new RuntimeException(e);
                }
            }

            return respuesta;
        }
    }

    public class TareaComprobarDisponibilidadJugadores extends OneShotBehaviour{

        private Partida partidaProponer;

        public TareaComprobarDisponibilidadJugadores(Partida partida) {
            partidaProponer = partida;
        }

        @Override
        public void action() {
            ACLMessage propuesta = new ACLMessage(ACLMessage.PROPOSE);

            IniciarPartida iniciarPartida = new IniciarPartida(partidaProponer);
            Action action = new Action(myAgent.getAID(), iniciarPartida);

            propuesta.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
            propuesta.setSender(myAgent.getAID());
            propuesta.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
            propuesta.setOntology(ontology.getName());

            try {
                for (int i = 0; i < listaAgentes.size(); i++) {
                    AgenteJugador jugador = (AgenteJugador) listaAgentes.get(i);
                    //propuesta.addReceiver(new AID(jugador.getApodo(), AID.ISLOCALNAME));
                    propuesta.addReceiver(jugador.getAgentePlataforma());
                }
                manager.fillContent(propuesta, action);
                addBehaviour(new TareaIniciarPartidaIniciador(myAgent, propuesta));
            } catch (Codec.CodecException | OntologyException e) {
                throw new RuntimeException(e);
            }

        }

    }

    public class TareaIniciarPartidaIniciador extends ProposeInitiator{

        public TareaIniciarPartidaIniciador(Agent a, ACLMessage msg) {
            super(a, msg);
        }

        @Override
        protected void handleAcceptProposal(ACLMessage accept_proposal) {
            String sender = accept_proposal.getSender().getLocalName();
            if(sender.contains("Espia") && !confirmacionEspia){
                confirmacionEspia = true;
                espia = new AgenteJugador();
                espia.setTipoJugador(ESPIA);
                espia.setApodo(sender);
            }else if (sender.contains("Seguridad") && !confirmacionSeguridad){
                confirmacionSeguridad = true;
                seguridad = new AgenteJugador();
                seguridad.setTipoJugador(TipoAgenteJugador.SEGURIDAD);
                seguridad.setApodo(sender);
            }
        }

        @Override
        protected void handleRejectProposal(ACLMessage reject_proposal) {
            String sender = reject_proposal.getSender().getLocalName();
            if(sender.contains("Espia")){
                confirmacionEspia = false;
            }else {
                confirmacionSeguridad = false;
            }
        }

        @Override
        protected void handleNotUnderstood(ACLMessage notUnderstood) {
            try {
                throw new NotUnderstoodException("No se ha entendido la Ontología");
            } catch (NotUnderstoodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public class ComprobarAgentesJugador extends TickerBehaviour{

        public ComprobarAgentesJugador(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            if(confirmacionEspia && confirmacionSeguridad){
                crearTablero();
                stop();
            }
        }

    }


    private void crearTablero() {

        //crear Tablero con la Partida y los Agentes
        Object[] args = new Object[6];
        args[0] = prop;
        args[1] = partida;
        args[2] = espia;
        args[3] = seguridad;
        args[4] = distribucionPartida;
        args[5] = this.getLocalName();

        try {

            ContainerController conC = this.getContainerController();
            AgentController ac = conC.createNewAgent("AgenteTablero-" + partida.getIdPartida(),
                    "es.ujaen.ssmmaa.curso2023_24.agentes.AgenteTablero", args);
            ac.start();
            ac.putO2AObject(this.getAID(), AgentController.ASYNC);


//                MicroRuntime.startAgent("AgenteTablero-" + partida.getIdPartida(),
//                        "es.ujaen.ssmmaa.curso2023_24.agentes.AgenteTablero", args);

            listaTableros.add("AgenteTablero-" + partida.getIdPartida());

            //TODO: comprobar si se creó la carpeta del juego, sino crearla
            //crear el archivo de la partida con el idPartida, el nombre de los espia y seguridad, y la distribucion de objetivos

            Path dir = Path.of("./juegos/" + partida.getIdJuego());

            myGUI.addPartida(partida.getIdJuego(), partida.getIdPartida(), dir);

            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String pathPartida = dir + "/" + partida.getIdPartida() + ".txt";

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathPartida, true))){
                writer.write(partida.getIdPartida() + ";" + espia.getApodo() + ";" + seguridad.getApodo() + ";" + distribucionPartida.toString());
                writer.newLine();
                //System.out.println("Archivo de la partida creado.");
            }
            catch(IOException ex){
                System.out.println("Ha ocurrido un error creando el archivo de la partida " + partida.getIdPartida() + ".");
                ex.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println("El Tablero de la Partida " + partida.getIdPartida() + " no se ha podido crear" + e.getMessage());
        }

    }

    public class TareaSubscripcionOrganizadorParticipante extends SubscriptionResponder{

        private Subscription suscripcionOrganizador;

        public TareaSubscripcionOrganizadorParticipante(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        public TareaSubscripcionOrganizadorParticipante(Agent a, MessageTemplate mt, SubscriptionManager sm) {
            super(a, mt, sm);
        }

        protected ACLMessage handleSubscription(ACLMessage subscription) throws NotUnderstoodException, RefuseException {
            try {
                String nombreAgente = subscription.getSender().getName();

                suscripcionOrganizador = createSubscription(subscription);
                if (!gestor.haySubscripcion(nombreAgente)) {
                    gestor.register(suscripcionOrganizador);
                }
                ACLMessage agree = subscription.createReply();
                agree.setPerformative(ACLMessage.AGREE);
                agree.setOntology(ontology.getName());
                agree.setLanguage(codec.getName());
                monitorOrg = subscription.getSender();
                // Justificación en la respuesta
                // TODO: cuando no es un agent action? en que se empaqueta? predicate?
                Justificacion justificacion = new Justificacion();
                justificacion.setMotivacion(Motivacion.SUBSCRIPCION_ACEPTADA);
                justificacion.setIdJuego("justificacion");
                // Agregar la justificación al mensaje
                manager.fillContent(agree, justificacion);
                return agree;

            } catch (OntologyException | Codec.CodecException e) {
                e.printStackTrace();
                throw new NotUnderstoodException("No se ha entendido la ontología");
            }
        }

        protected ACLMessage handleCancel(ACLMessage cancel) throws FailureException {
            String nombreAgente = cancel.getSender().getName();
            suscripcionOrganizador = gestor.getSubscripcion(nombreAgente);
            gestor.deregister(suscripcionOrganizador);
            return cancel;
        }

    }

    public class TareaAvisoMonitor extends OneShotBehaviour{

        SubInform informe;

        public TareaAvisoMonitor(SubInform informe) {
            this.informe = informe;
        }

        @Override
        public void action() {
            AID monitor = monitorOrg;

            Subscription suscripcion = gestor.getSubscripcion(monitor.getName());

            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setLanguage(codec.getName());

            if (informe instanceof IncidenciaJuego) {
                IncidenciaJuego incidencia = (IncidenciaJuego) informe;
                msg.setOntology(ontology.getName());
                try {
                    manager.fillContent(msg, incidencia);
                } catch (Codec.CodecException | OntologyException ex) {
                    Logger.getLogger(AgenteOrganizador.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                ResultadoJuego resultadoJuego = (ResultadoJuego) informe;
                msg.setOntology(ontology.getName());
                try {
                    manager.fillContent(msg, resultadoJuego);
                } catch (Codec.CodecException | OntologyException ex) {
                    Logger.getLogger(AgenteOrganizador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            suscripcion.notify(msg);

        }
    }

    public class TareaRecibirPasosPartida extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate msgTemplatePasos = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage respuesta = receive(msgTemplatePasos);

            if (respuesta != null) {
                if(Objects.equals(respuesta.getConversationId(), "EXPLORACION")){
                    tratamientoPasos(respuesta);
                }
                if(Objects.equals(respuesta.getConversationId(), "ESTRATEGIA")){
                    tratamientoPasos(respuesta);
                }
                if(Objects.equals(respuesta.getConversationId(), "PUNTUACION")){
                    tratamientoPasos(respuesta);
                }

            } else {
                block();
            }
        }

        private void tratamientoPasos(ACLMessage respuesta) {
            String[] contenido = respuesta.getContent().split(";", 3);
            System.out.println(contenido[0] + contenido[1] + contenido[2]);
            String idJuego = contenido[0];
            String idPartida = contenido[1];
            String mensaje = contenido[2];
            registrarArchivoPartida(idJuego, idPartida, mensaje);
        }
    }

    private void registrarArchivoPartida(String idJuego, String idPartida, String mensaje){
        Path dir = Path.of("./juegos/" + idJuego);
        String pathPartida = dir + "/" + idPartida + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathPartida, true))){
            writer.write(mensaje);
            writer.newLine();
            //System.out.println("Archivo de la partida modificado.");
        }
        catch(IOException ex){
            System.out.println("Ha ocurrido un error modificando el archivo de la partida " + partida.getIdPartida() + ".");
            ex.printStackTrace();
        }

    }

}
