<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
        </main>
    </div>
</div>

<script>
    window.contextPath = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
<c:if test="${activeMenu == 'attendance'}">
    <script src="${pageContext.request.contextPath}/assets/js/attendance.js"></script>
</c:if>
</body>
</html>
