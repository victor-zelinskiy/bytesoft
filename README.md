# Реализация ObjectDao

Реализация через аннотации и вставку данных из БД в помеченные аннотацией поля, через рефлексию.

Аннотация com.edu.nc.bytesoft.dao.annotation.AttributeName, для отметки полей и указания соответствующего названия атрибута из БД.
Пример использования:
```java
public class User extends Contact {
    @AttributeName("USR_LOGIN")
    protected String login;

    @AttributeName("USR_PASSWORD")
    protected String password;

    @AttributeName("USR_REG_DATE")
    protected Date registered;

    @AttributeName("USR_COMPANY_NAME")
    protected String companyName;

    @AttributeName("USR_ROLES")
    protected List<Role> roles;

    @AttributeName("USR_CONTACTS")
    protected List<Contact> contacts;
    }
    
    public class NamedEntity extends BaseEntity {
    @AttributeName({"AIT_NAME", "USR_NAME", "ICN_NAME", "IPN_VALUE"})
    protected String name;
    }
```

Используемый запрос для извлечения данных объекта из БД где '?' - ид извлекаемого объекта:
```sql
SELECT ATYPE.CODE, ATTS.VALUE, ATTS.DATE_VALUE, OBJR.REFERENCE
FROM ATTRTYPE ATYPE
LEFT JOIN ATTRIBUTES ATTS ON (ATTS.ATTR_ID = ATYPE.ATTR_ID AND ATTS.OBJECT_ID = ?)
LEFT JOIN OBJREFERENCE OBJR ON (OBJR.ATTR_ID = ATYPE.ATTR_ID AND OBJR.OBJECT_ID = ?)
            WHERE ATYPE.OBJECT_TYPE_ID IN 
              (
                SELECT OBT.OBJECT_TYPE_ID
                FROM OBJTYPE OBT
                START WITH OBT.OBJECT_TYPE_ID = 
                  (
                   SELECT OBJECT_TYPE_ID
                    FROM OBJECTS
                    WHERE OBJECT_ID = ?
                 )
                CONNECT BY PRIOR OBT.PARENT_ID = OBT.OBJECT_TYPE_ID
              )
            AND (ATTS.VALUE IS NOT NULL 
            OR ATTS.DATE_VALUE IS NOT NULL
            OR OBJR.REFERENCE IS NOT NULL);
```

Класс ObjectDaoReflect, по умолчанию рекурсивно заполняет все объектные поля объекта, при использовании с флагом isLazy = true, объектные поля заполняет объектами с одним заполненным полем id и с меткой lazy потом при обращении к таким объектам надо сделать проверку на lazy, и если она = true вызвать для него заполнение по его id. Пример не ленивого использования:
```java
    @Test
    public void getUserTest() throws Exception {
        String user1Name = "Alexander Hunold";
        String user2Name = "Steven King";
        ObjectDao<User> userDao = new ObjectDaoReflect<>(User.class, dataSource.getConnection());
        User user1 = userDao.getObjectById(24); //Alexander Hunold
        System.out.println(user1);
        assertThat(user1.getName()).isEqualTo(user1Name);

        User user2 = userDao.getObjectById(22); //Steven King
        System.out.println(user2);
        assertThat(user2.getName()).isEqualTo(user2Name);
    }
```

результат выполнения:
``` tex
User{Contact{
id=24 , name='Alexander Hunold', email='AlexanderHunold@mail.ru', photo=null, phones=[+3085410111]} , login='Alexander', password='Hunold', registered=Sun May 16 00:00:00 EEST 1999, companyName='null', roles=[ROLE_PROJECT_MANAGER], contacts=null} 
User{Contact{
id=22 , name='Steven King', email='StevenKing@mail.ru', photo=null, phones=[+3085411111]} , login='Steven', password='King', registered=Fri Jan 01 00:00:00 EET 1999, companyName='null', roles=[ROLE_PROJECT_MANAGER, ROLE_CUSTOMER], contacts=null} 

```

```java
    @Test
    public void getTaskTest() throws Exception {
        String taskName = "Task 1 Name. Project 1";
        ObjectDao<Task> taskDao = new ObjectDaoReflect<>(Task.class, dataSource.getConnection());
        Task task = taskDao.getObjectById(78);
        System.out.println(task);
        assertThat(task.getName()).isEqualTo(taskName);
    }
```
Результат выполнения:
```tex
Task{
id=78 , name='Task 2 Name. Project 1'
author=User{Contact{
id=26 , name='Valli Pataballa', email='ValliPataballa@mail.ru', photo=null, phones=[+3085410111]} , login='Valli', password='Pataballa', registered=Fri Aug 12 00:00:00 EEST 1994, companyName='null', roles=[ROLE_TEAM_LEAD], contacts=null} , createdDate=Thu Oct 16 00:00:00 EEST 2014, description='Second task for Module 1', status=STATUS_CLOSED, deadlineDate=Mon Nov 16 00:00:00 EET 2015, completedDate=null
assignUser=[User{Contact{
id=39 , name='Karen Colmenares', email='KarenColmenares@mail.ru', photo=null, phones=[+3085410111]} , login='Karen', password='Colmenares', registered=Wed Dec 27 00:00:00 EET 2000, companyName='null', roles=[ROLE_DEVELOPER], contacts=null} ], priority=4, 
watchers=[User{Contact{
id=23 , name='Neena Kochhar', email='NeenaKochhar@mail.ru', photo=null, phones=[+3085410111]} , login='Neena', password='Kochhar', registered=Thu Apr 01 00:00:00 EEST 1999, companyName='null', roles=[ROLE_PROJECT_MANAGER], contacts=null} ], comments=null, parentTask=null} 
```

Ленивое использование:
```java
    @Test
    public void lazyUsageExample() throws Exception {
        String taskName = "Task 2 Name. Project 1";
        ObjectDao<Task> taskDao = new ObjectDaoReflect<>(Task.class, dataSource.getConnection(), true);
        Task task = taskDao.getObjectById(78);
        System.out.println(task);
        assertThat(task.getName()).isEqualTo(taskName);
    }
```

Результат ленивого использования:
```tex
Task{
id=78 , name='Task 2 Name. Project 1'
author=User{Contact{
id=26 , name='null', email='null', photo=null, phones=null} , login='null', password='null', registered=null, companyName='null', roles=null, contacts=null} , createdDate=Thu Oct 16 00:00:00 EEST 2014, description='Second task for Module 1', status=STATUS_CLOSED, deadlineDate=Mon Nov 16 00:00:00 EET 2015, completedDate=null
assignUser=[User{Contact{
id=39 , name='null', email='null', photo=null, phones=null} , login='null', password='null', registered=null, companyName='null', roles=null, contacts=null} ], priority=4,
watchers=[User{Contact{
id=23 , name='null', email='null', photo=null, phones=null} , login='null', password='null', registered=null, companyName='null', roles=null, contacts=null} ], comments=null, parentTask=null} 
```

Класс TransactionManager для выполнения операций разных dao в одной транзакции, dao должны реализовывать интерфейс SettableConnection.
Пример использования:
```java
    @Test
    public void transactionalUsageExample() throws Exception {
        TransactionManager transactionManager = new TransactionManager();
        ObjectDao<Task> taskDao = new ObjectDaoReflect<>(Task.class, transactionManager);
        ObjectDao<User> userDao = new ObjectDaoReflect<>(User.class, transactionManager);

        try (TransactionManager.Transaction transaction = transactionManager.startTransaction()) {
            try {
                Task task = taskDao.getObjectById(77);
                User user = userDao.getObjectById(22);
                transaction.commit();
            } catch (SQLException | NoSuchObjectException e) {
                transaction.rollback();
            }
        }
    }
```

 














