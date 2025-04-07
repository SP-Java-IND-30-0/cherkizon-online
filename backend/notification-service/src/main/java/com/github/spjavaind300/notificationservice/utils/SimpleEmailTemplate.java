package com.github.spjavaind300.notificationservice.utils;

import com.github.spjavaind300.notificationservice.model.NotificationType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

import static com.github.spjavaind300.notificationservice.model.NotificationType.*;

@Component
public class SimpleEmailTemplate {
    private final Map<NotificationType, String> simpleTemplates = new EnumMap<>(NotificationType.class);

    public SimpleEmailTemplate() {
        simpleTemplates.put(USER_CREATED, GREETING_TEMPLATE);
        simpleTemplates.put(ADV_UPDATED, ADV_UPDATED_TEMPLATE);
        simpleTemplates.put(COMMENT_CREATED, COMMENT_CREATED_TEMPLATE);
    }

    public String getTemplate(NotificationType type) {
        return simpleTemplates.get(type);
    }

    private static final String GREETING_TEMPLATE = """
            <div style="display: none;">%s</div>
            <div>
                <h1>Добро пожаловать! 🌟</h1>
                <p>Привет, %s!</p>
                <p>Мы так рады, что вы решили присоединиться к нашему сообществу. Теперь у вас есть доступ ко всем возможностям, которые мы подготовили с любовью.</p>
                <p>Если у вас есть вопросы или нужна помощь — просто нажмите кнопку ниже. Мы всегда рядом!</p>
                <p style="text-align: center;">
                    <a href="%s" class="button">Написать в поддержку</a>
                </p>
                <p>С теплом,<br>Команда %s</p>
                <div class="footer">
                    <p>Если это письмо пришло вам по ошибке, просто проигнорируйте его.</p>
                </div>
            </div>
            """;

    private static final String COMMENT_CREATED_TEMPLATE = """
            <div style="display: none;">%s</div>
            <div>
                 <h1 >Кто-то оставил вам комментарий! 💬</h1>
                 <p>Привет, %s!</p>
                 <p>Пользователь <span>%s</span> только что оставил комментарий к вашему объявлению <strong>"%s"</strong>:</p>
            
                 <div>
                     <p><a href="%s">Читать полностью и ответить →</a></p>
                 </div>
            
                 <p>Не оставляйте его без внимания — возможно, это начало интересного диалога!</p>
                 <p>С наилучшими пожеланиями,<br>Команда %s</p>
                 <div>
                     <p><a href="%s">Написать в поддержку</a></p>
                 </div>
            </div>
            """;

    private static final String ADV_UPDATED_TEMPLATE = """
            <div style="display: none;">%s</div>
            <div>
                <h1>Кое-что изменилось! ✨</h1>
                <p>Привет, %s!</p>
                <p>Вы оставляли комментарий под объявлением <strong>"%s"</strong>, и мы хотели сообщить, что автор внес в него изменения.</p>
            
                <div>
                    <h3>%s</h3>
                    <p><a href="%s">Посмотреть обновленное объявление →</a></p>
                </div>
            
                <p>Возможно, теперь там появилось что-то интересное именно для вас!</p>
                <p>С уважением,<br>Команда %s</p>
                <div>
                    <p>Хотите меньше уведомлений? <a href="%s">Написать в поддержку</a></p>
                </div>
            </div>
            """;
}

