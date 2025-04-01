package com.github.spjavaind300.notificationservice.utils;

import com.github.spjavaind300.notificationservice.model.NotificationType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.github.spjavaind300.notificationservice.model.NotificationType.*;

@Component
public class SimpleEmailTemplate {
    private final Map<NotificationType, EmailTemplateProvider> simpleTemplates = new HashMap<>();

    public SimpleEmailTemplate() {
        simpleTemplates.put(USER_CREATED, this::getGreetingTemplate);
        simpleTemplates.put(ADV_UPDATED, this::getAdvUpdatedTemplate);
        simpleTemplates.put(COMMENT_CREATED, this::getCommentCreatedTemplate);
    }

    public String getTemplate(NotificationType type) {
        return simpleTemplates.get(type).getTemplate();
    }

    private String getGreetingTemplate() {
        return GREETING_TEMPLATE;
    }

    private String getCommentCreatedTemplate() {
        return COMMENT_CREATED_TEMPLATE;
    }

    private String getAdvUpdatedTemplate() {
        return ADV_UPDATED_TEMPLATE;
    }


    @FunctionalInterface
    private interface EmailTemplateProvider {
        String getTemplate();
    }

    private static final String GREETING_TEMPLATE = """
            <div>
                <h1>Добро пожаловать! 🌟</h1>
                <p>Привет, {{username}}!</p>
                <p>Мы так рады, что вы решили присоединиться к нашему сообществу. Теперь у вас есть доступ ко всем возможностям, которые мы подготовили с любовью.</p>
                <p>Если у вас есть вопросы или нужна помощь — просто нажмите кнопку ниже. Мы всегда рядом!</p>
                <p style="text-align: center;">
                    <a href="{{supportLink}}" class="button">Написать в поддержку</a>
                </p>
                <p>С теплом,<br>Команда {{serviceName}}</p>
                <div class="footer">
                    <p>Если это письмо пришло вам по ошибке, просто проигнорируйте его.</p>
                </div>
            </div>
            """;

    private static final String COMMENT_CREATED_TEMPLATE = """
            <div>
                 <h1 >Кто-то оставил вам комментарий! 💬</h1>
                 <p>Привет, {{username}}!</p>
                 <p>Пользователь <span>{{commentAuthor}}</span> только что оставил комментарий к вашему объявлению <strong>"{{adTitle}}"</strong>:</p>
            
                 <div>
                     <p><em>"{{commentPreview}}..."</em></p>
                     <p><a href="{{commentLink}}">Читать полностью и ответить →</a></p>
                 </div>
            
                 <p>Не оставляйте его без внимания — возможно, это начало интересного диалога!</p>
                 <p>С наилучшими пожеланиями,<br>Команда {{serviceName}}</p>
                 <div>
                     <p><a href="{{unsubscribeLink}}">Отписаться от уведомлений о комментариях</a></p>
                 </div>
            </div>
            """;

    private static final String ADV_UPDATED_TEMPLATE = """
            <div>
                <h1>Кое-что изменилось! ✨</h1>
                <p>Привет, {{username}}!</p>
                <p>Вы оставляли комментарий под объявлением <strong>"{{adTitle}}"</strong>, и мы хотели сообщить, что автор внес в него изменения.</p>
            
                <div>
                    <h3>{{adTitle}}</h3>
                    <p>{{adUpdates}}</p>
                    <p><a href="{{adLink}}">Посмотреть обновленное объявление →</a></p>
                </div>
            
                <p>Возможно, теперь там появилось что-то интересное именно для вас!</p>
                <p>С уважением,<br>Команда {{serviceName}}</p>
                <div>
                    <p>Хотите меньше уведомлений? <a href="{{notificationSettings}}">Настроить рассылку</a></p>
                </div>
            </div>
            """;
}

