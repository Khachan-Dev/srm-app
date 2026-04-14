# 1. اختار نسخة Java
FROM eclipse-temurin:17-jdk-slim

# 2. حدد مجلد العمل وسط السيرفر
WORKDIR /app

# 3. كوبي كاع الملفات للمجلد
COPY . .

# 4. عطي صلاحية تنفيذ لـ Maven
RUN chmod +x mvnw

# 5. صاوب المشروع (Build)
RUN ./mvnw clean install -DskipTests

# 6. حدد البورت اللي غيخدم فيه السيت
EXPOSE 8080

# 7. الأمر اللي غيشغل السيت فاش يسالي الـ Build
ENTRYPOINT ["java","-jar","target/*.jar"]