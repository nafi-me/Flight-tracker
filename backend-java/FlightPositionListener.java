@Component
public class FlightPositionListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "flight_positions")
    public void listen(String msg) {

        messagingTemplate.convertAndSend("/topic/positions", msg);

    }
}
