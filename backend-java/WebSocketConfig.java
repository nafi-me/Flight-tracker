@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new FlightSocketHandler(), "/ws/flights")
                .setAllowedOrigins("*");
    }
}
