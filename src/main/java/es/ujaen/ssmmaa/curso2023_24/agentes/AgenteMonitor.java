package es.ujaen.ssmmaa.curso2023_24.agentes;

import es.ujaen.ssmmaa.OntoJuegoEspias;
import es.ujaen.ssmmaa.curso2023_24.Constantes;
import es.ujaen.ssmmaa.elementos.AgenteOrganizador;
import es.ujaen.ssmmaa.elementos.*;
import jade.content.AgentAction;
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
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ProposeInitiator;
import jade.proto.SubscriptionInitiator;
import jade.util.leap.List;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static es.ujaen.ssmmaa.Vocabulario.*;
import static es.ujaen.ssmmaa.Vocabulario.TipoAgenteJugador.ESPIA;
import static es.ujaen.ssmmaa.Vocabulario.TipoAgenteJugador.SEGURIDAD;
import static es.ujaen.ssmmaa.Vocabulario.TipoAgenteOrganizador.MONITOR;
import static es.ujaen.ssmmaa.Vocabulario.TipoAgenteOrganizador.ORGANIZADOR;


public class AgenteMonitor extends Agent {


    private final Properties prop = new Properties();
    private String nombreArchivoConfiguracion = "";
    //protected static MonitorJFrame myGUI;
    private ArrayList<String> listaEspias;
    private ArrayList<String> listaSeguridad;
    private HashMap<String, List> listaEspiasDisponiblesPorJuego;
    private HashMap<String, List> listaSeguridadDisponiblesPorJuego;
    private ArrayList<HashMap<AID, TareaSuscripcionOrganizadorIniciador>> suscripciones;
    private ArrayList<AID> agentes;


    private Ontology ontology;
    private ContentManager manager;
    private final Codec codec = new SLCodec();
    
    //TODO: Para organizar un solo juego BORRAME cuendo no sea necesaria
    private boolean unoSolo;


    @Override
    protected void setup() {
        //Inicialización de las variables del agente
        Object[] args = getArguments();
        if (args != null) {
            for (Object arg : args) {
                nombreArchivoConfiguracion = (String) arg;
            }
            System.out.println("Se ha cargado el archivo de configuracion");
        }
        this.unoSolo = true;
        inicializarMonitor();


        //Registro del agente en las Páginas Amarrillas
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(MONITOR.name());
        sd.setName(NOMBRE_SERVICIO);
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.getMessage();
        }

        crearAgentes();

        //Configuración del GUI
        //inicializarGUI();

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
        addBehaviour(new TareaProponerJuego(this, 10000));
        addBehaviour(new tareaOrganizarJuego(this, 10000));
        addBehaviour(new TareaSuscripcionOrganizador());

        addBehaviour(new TareaSolicitudFinalizacion());
    }

    private void crearAgentes() {
        crearOrganizadores();
        crearJugadores();
    }


    private void crearJugadores() {
        int min = Integer.parseInt(prop.getProperty("MIN_JUGADORES"));
        int max = Integer.parseInt(prop.getProperty("MAX_JUGADORES"));
        int numJugadores = Constantes.aleatorio.nextInt(min, max);
        Object[] args = new Object[1];
        args[0] = prop;
        for (int i = 0; i < numJugadores; i++) {
            try {
                ContainerController conC = this.getContainerController();
                AgentController ac = conC.createNewAgent("AgenteEspia-" + i,
                        "es.ujaen.ssmmaa.curso2023_24.agentes.AgenteEspia", args);
                ac.start();
                ac.putO2AObject(this.getAID(), AgentController.ASYNC);


//                MicroRuntime.startAgent("AgenteEspia-" + i,
//                        "es.ujaen.ssmmaa.curso2023_24.agentes.AgenteEspia", args);

                AgentController ac2 = conC.createNewAgent("AgenteSeguridad-" + i,
                        "es.ujaen.ssmmaa.curso2023_24.agentes.AgenteSeguridad", args);
                ac2.start();
                ac2.putO2AObject(this.getAID(), AgentController.ASYNC);


//                MicroRuntime.startAgent("AgenteSeguridad-" + i,
//                        "es.ujaen.ssmmaa.curso2023_24.agentes.AgenteSeguridad", args);


            } catch (Exception e) {
                System.out.println("El Jugador " + i + " no se ha podido crear" + e.getMessage());
            }
        }
    }

    private void crearOrganizadores() {
        int min = Integer.parseInt(prop.getProperty("MIN_ORGANIZADORES"));
        int max = Integer.parseInt(prop.getProperty("MAX_ORGANIZADORES"));
        int numOrganizadores = Constantes.aleatorio.nextInt(min, max);
        Object[] args = new Object[1];
        args[0] = prop;
        for (int i = 0; i < numOrganizadores; i++) {
            try {
                ContainerController conC = this.getContainerController();
                AgentController ac = conC.createNewAgent("AgenteOrganizador-" + i,
                        "es.ujaen.ssmmaa.curso2023_24.agentes.AgenteOrganizador", args);
                ac.start();
                ac.putO2AObject(this.getAID(), AgentController.ASYNC);


//                MicroRuntime.startAgent("AgenteOrganizador-" + i,
//                        "es.ujaen.ssmmaa.curso2023_24.agentes.AgenteOrganizador", args);


            } catch (Exception e) {
                System.out.println("El Organizador " + i + " no se ha podido crear" + e.getMessage());
            }
        }
    }


    private void inicializarMonitor() {
        InputStream input = null;

        try {
            input = this.getClass().getClassLoader().getResourceAsStream(nombreArchivoConfiguracion);
            prop.load(input);
        } catch (IOException ex) {
            System.out.println("El archivo de Configuración no se ha encontrado " + ex.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        listaEspias = new ArrayList<>();
        listaSeguridad = new ArrayList<>();
        listaEspiasDisponiblesPorJuego = new HashMap<>();
        listaSeguridadDisponiblesPorJuego = new HashMap<>();
        suscripciones = new ArrayList<>();
        for (int i = 0; i < TipoIncidencia.values().length; i++) {
            HashMap<AID, TareaSuscripcionOrganizadorIniciador> aux = new HashMap<>();
            suscripciones.add(aux);
        }
        agentes = new ArrayList<>();
    }
//
//    private void inicializarGUI() {
//        myGUI = new MonitorJFrame(this);
//        myGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        myGUI.pack();
//        myGUI.setVisible(true);
//        myGUI.setSize(1200, 800);
//    }

    @Override
    protected void takeDown() {
        cancelarSuscripciones();
        //Eliminar registro del agente en las Páginas Amarillas
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.getMessage();
        }
        //Liberación de recursos, incluido el GUI

        //myGUI.dispose();
        //Despedida
        System.out.println("Finaliza la ejecución del agente: " + this.getName());
    }

    public void cancelarSuscripciones() {
        for (int i = 0; i < TipoIncidencia.values().length; i++) {
            for (int j = 0; j < TipoIncidencia.values().length; j++) {
                TareaSuscripcionOrganizadorIniciador sub = suscripciones.get(i).remove(agentes.get(j));
                if (sub != null){
                    System.out.println("Se solicita la cancelacion de la suscripcion de " + agentes.get(j));
                    sub.cancel(agentes.get(j), true);
                }
            }
        }
    }

    public class TareaProponerJuego extends TickerBehaviour {

        public TareaProponerJuego(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            //if(unoSolo){
            DFAgentDescription templateEspia = new DFAgentDescription();
            ServiceDescription templatesdEspia = new ServiceDescription();
            templatesdEspia.setType(ESPIA.name());
            templateEspia.addServices(templatesdEspia);

            DFAgentDescription templateSeguridad = new DFAgentDescription();
            ServiceDescription templatesdSeguridad = new ServiceDescription();
            templatesdSeguridad.setType(SEGURIDAD.name());
            templateSeguridad.addServices(templatesdSeguridad);

            String idJuego = "Juego-" + UUID.randomUUID();
            //System.out.println(idJuego);
            List listaMapaEspias = new jade.util.leap.ArrayList();
            List listaMapaSeguridad = new jade.util.leap.ArrayList();
            listaEspiasDisponiblesPorJuego.put(idJuego, listaMapaEspias);
            listaSeguridadDisponiblesPorJuego.put(idJuego, listaMapaSeguridad);

            listaEspias.clear();
            listaSeguridad.clear();

            try {
                DFAgentDescription[] resultEspia = DFService.search(myAgent, templateEspia);
                for (DFAgentDescription espia : resultEspia) {
                    listaEspias.add(espia.getName().getLocalName());
                }

                DFAgentDescription[] resultSeguridad = DFService.search(myAgent, templateSeguridad);
                for (DFAgentDescription seguridad : resultSeguridad) {
                    listaSeguridad.add(seguridad.getName().getLocalName());
                }

            } catch (FIPAException fe) {
                fe.getMessage();
            }

            if (!listaEspias.isEmpty()) {
                for (String espia : listaEspias) {
                    ProponerJuego proponer = new ProponerJuego(new Juego(idJuego, ModoJuego.INDIVIDUAL));
                    Action actionproponer = new Action(myAgent.getAID(), proponer);
                    ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
                    msg.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                    msg.setSender(myAgent.getAID());
                    msg.addReceiver(new AID(espia, AID.ISLOCALNAME));
                    msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
                    msg.setOntology(ontology.getName());
                    try {
                        manager.fillContent(msg, actionproponer);
                        //System.out.println("Proponiendo Juego a Espia " + espia);
                    } catch (Codec.CodecException | OntologyException e) {
                        throw new RuntimeException(e);
                    }

                    addBehaviour(new TareaProponerJuegoIniciador(myAgent, msg));
                }
            } else {
                //System.out.println("No hay espias.");
            }

            if (!listaSeguridad.isEmpty()) {
                for (String seguridad : listaSeguridad) {
                    AgentAction proponer = new ProponerJuego(new Juego(idJuego, ModoJuego.INDIVIDUAL));
                    Action actionproponer = new Action(myAgent.getAID(), proponer);
                    ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
                    msg.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                    msg.setSender(myAgent.getAID());
                    msg.addReceiver(new AID(seguridad, AID.ISLOCALNAME));
                    msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
                    msg.setOntology(ontology.getName());
                    try {
                        manager.fillContent(msg, actionproponer);
                        //System.out.println("Proponiendo Juego a Seguridad " + seguridad);
                    } catch (Codec.CodecException | OntologyException e) {
                        throw new RuntimeException(e);
                    }

                    addBehaviour(new TareaProponerJuegoIniciador(myAgent, msg));
                }
            } else {
                //System.out.println("No hay seguridad.");
            }

        //}
        }
    }

    public class TareaProponerJuegoIniciador extends ProposeInitiator {

        public TareaProponerJuegoIniciador(Agent a, ACLMessage msg) {
            super(a, msg);
        }

        @Override
        protected void handleAcceptProposal(ACLMessage accept_proposal) {
            try {
                Registro registro = (Registro) manager.extractContent(accept_proposal);
                String idJuego = registro.getIdJuego();
                AgenteJugador agenteJugador = (AgenteJugador) registro.getAgenteJuego();
                if (agenteJugador.getTipoJugador() == ESPIA) {
                    listaEspiasDisponiblesPorJuego.get(idJuego).add(agenteJugador);
                    //System.out.println("Agente Espia " + agenteJugador + " ha aceptado jugar");
                }
                if (agenteJugador.getTipoJugador() == SEGURIDAD) {
                    listaSeguridadDisponiblesPorJuego.get(idJuego).add(agenteJugador);
                    //System.out.println("Agente Seguridad " + agenteJugador + " ha aceptado jugar");
                }

            } catch (Codec.CodecException | OntologyException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void handleRejectProposal(ACLMessage reject_proposal) {
            try {
                Justificacion justificacion = (Justificacion) manager.extractContent(reject_proposal);
                String idJuego = justificacion.getIdJuego();
                Motivacion motivacion = justificacion.getMotivacion();
                System.out.println("Rechazado participar en el juego " + idJuego + " debido a " + motivacion.name());
            } catch (Codec.CodecException | OntologyException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void handleNotUnderstood(ACLMessage notUnderstood) {
            try {
                throw new NotUnderstoodException(notUnderstood);
            } catch (NotUnderstoodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public class tareaOrganizarJuego extends TickerBehaviour {

        public tareaOrganizarJuego(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            //if(unoSolo){
            ArrayList<String> idsJuegos = new ArrayList<>(listaEspiasDisponiblesPorJuego.keySet());
            int tam = ModoJuego.INDIVIDUAL.getMinJugadores();
            String idJuego = mayorNumEspias(idsJuegos, tam);

            List espias = listaEspiasDisponiblesPorJuego.get(idJuego);
            List seguridad = listaSeguridadDisponiblesPorJuego.get(idJuego);

            if (idJuego != null) {
                if (listaSeguridadDisponiblesPorJuego.get(idJuego).size() >= tam) {
                    List lista = new jade.util.leap.ArrayList();
                    for (int i = 0; i < tam; i++) {
                        lista.add(espias.get(aleatorio.nextInt(espias.size())));
                        lista.add(seguridad.get(aleatorio.nextInt(seguridad.size())));
                    }
                    Juego juego = new Juego(idJuego, ModoJuego.INDIVIDUAL);
                    InfoPartida infoPartida = new InfoPartida(PUNTOS_ACCION, NUM_ZONAS);
                    OrganizarJuego organizarJuego = new OrganizarJuego(juego, infoPartida, lista);
                    Action actionorganizar = new Action(myAgent.getAID(), organizarJuego);

                    DFAgentDescription templateOrganizador = new DFAgentDescription();
                    ServiceDescription templatesdOrganizador = new ServiceDescription();
                    templatesdOrganizador.setType(ORGANIZADOR.name());
                    templateOrganizador.addServices(templatesdOrganizador);

                    String organizador = "";
                    try {
                        DFAgentDescription[] resultOrganizador = DFService.search(myAgent, templateOrganizador);
                        organizador = resultOrganizador[aleatorio.nextInt(resultOrganizador.length)].getName().getLocalName();
                    } catch (FIPAException fe) {
                        fe.getMessage();
                    }

                    ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
                    msg.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                    msg.setSender(myAgent.getAID());
                    msg.addReceiver(new AID(organizador, AID.ISLOCALNAME));
                    msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
                    msg.setOntology(ontology.getName());

                    try {
                        manager.fillContent(msg, actionorganizar);
                        System.out.println("· Organizando Juego ·");
                    } catch (Codec.CodecException | OntologyException e) {
                        throw new RuntimeException(e);
                    }

                    addBehaviour(new TareaOrganizarJuegoIniciador(myAgent, msg));
                }
            }
        //}
        }


        private String mayorNumEspias(ArrayList<String> idsJuegos, int tam) {
            String aux = null;

            for (String idJuego : idsJuegos) {
                if (listaEspiasDisponiblesPorJuego.containsKey(idJuego) && listaEspiasDisponiblesPorJuego.get(idJuego).size() >= tam) {
                    tam = listaEspiasDisponiblesPorJuego.get(idJuego).size();
                    aux = idJuego;
                }
            }
            return aux;
        }
    }

    public class TareaOrganizarJuegoIniciador extends ProposeInitiator {

        public TareaOrganizarJuegoIniciador(Agent a, ACLMessage msg) {
            super(a, msg);
        }

        @Override
        protected void handleAcceptProposal(ACLMessage accept_proposal) {
            Registro registro;
            try {
                registro = (Registro) manager.extractContent(accept_proposal);
            } catch (Codec.CodecException | OntologyException e) {
                throw new RuntimeException(e);
            }
            String idJuego = registro.getIdJuego();
            AgenteOrganizador agenteOrganizador = (AgenteOrganizador) registro.getAgenteJuego();
            System.out.println("Se ha organizado un juego con id " + idJuego + " por el agente " + agenteOrganizador.getApodo());
            unoSolo = false;
            listaEspiasDisponiblesPorJuego.remove(idJuego);
            listaSeguridadDisponiblesPorJuego.remove(idJuego);

        }

        @Override
        protected void handleRejectProposal(ACLMessage reject_proposal) {
            try {
                Justificacion justificacion = (Justificacion) manager.extractContent(reject_proposal);
                String idJuego = justificacion.getIdJuego();
                Motivacion motivacion = justificacion.getMotivacion();
                System.out.println("No se ha podido organizar el juego  " + idJuego + " debido a " + motivacion.name());
            } catch (Codec.CodecException | OntologyException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void handleNotUnderstood(ACLMessage notUnderstood) {
            try {
                throw new NotUnderstoodException(notUnderstood);
            } catch (NotUnderstoodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public class TareaSuscripcionOrganizador extends OneShotBehaviour {


        @Override
        public void action() {


            DFAgentDescription templateOrganizador = new DFAgentDescription();
            ServiceDescription templatesdOrganizador = new ServiceDescription();
            templatesdOrganizador.setType(ORGANIZADOR.name());
            templateOrganizador.addServices(templatesdOrganizador);

            DFAgentDescription[] resultOrganizador;
            try {
                resultOrganizador = DFService.search(myAgent, templateOrganizador);
            } catch (FIPAException e) {
                throw new RuntimeException(e);
            }

            for (int i = 0; i < TipoIncidencia.values().length; i++) {

                //ideal pedir la suscripcion cuando monitor ha encargado un juego a organizador, asi se asegura de que organizador esta activo


                AgentAction registrarResultado = new RegistrarIncidencia(TipoIncidencia.values()[i]);
                Action accion = new Action(myAgent.getAID(), registrarResultado);

                for (DFAgentDescription dfa : resultOrganizador) {
                    ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
                    msg.setProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE);
                    msg.setSender(myAgent.getAID());
                    msg.addReceiver(dfa.getName());
                    msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
                    msg.setOntology(ontology.getName());
                    try {
                        manager.fillContent(msg, accion);
                    } catch (Codec.CodecException | OntologyException e) {
                        throw new RuntimeException(e);
                    }

                    TareaSuscripcionOrganizadorIniciador sub = new TareaSuscripcionOrganizadorIniciador(myAgent, msg);
                    suscripciones.get(i).put(dfa.getName(), sub);
                    agentes.add(dfa.getName());
                    addBehaviour(sub);
                }
            }
        }
    }

    public class TareaSuscripcionOrganizadorIniciador extends SubscriptionInitiator {


        public TareaSuscripcionOrganizadorIniciador(Agent a, ACLMessage msg) {
            super(a, msg);
        }

        @Override
        protected void handleAgree(ACLMessage agree) {
            try {
                Justificacion justificacion = (Justificacion) manager.extractContent(agree);
                String idJuego = justificacion.getIdJuego();
                Motivacion motivacion = justificacion.getMotivacion();
                System.out.println("Suscripcion aceptada  " + idJuego + " debido a " + motivacion.name());
            } catch (Codec.CodecException | OntologyException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            try {
                SubInform informe = (SubInform) manager.extractContent(inform);
                if (informe instanceof ResultadoJuego resultadoJuego) {
                    System.out.println(resultadoJuego);
                } else if (informe instanceof IncidenciaJuego incidenciaJuego) {
                    System.out.println(incidenciaJuego);
                }

            } catch (Codec.CodecException | OntologyException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void handleRefuse(ACLMessage refuse) {
            System.out.println("No se ha podido crear la suscripcion");
        }

        @Override
        protected void handleNotUnderstood(ACLMessage notUnderstood) {
            try {
                throw new NotUnderstoodException(notUnderstood);
            } catch (NotUnderstoodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public class TareaSolicitudFinalizacion extends CyclicBehaviour {

        @Override
        public void action() {

            MessageTemplate template = MessageTemplate.MatchConversationId("TERMINAR EJECUCION");
            ACLMessage respuesta = receive(template);

            if (respuesta != null) {
                myAgent.doDelete();
            } else {
                block();
            }
        }
    }

}
