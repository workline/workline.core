-- You can use this file to load seed data into the database using SQL statements
insert into member (id, name, email, phone_number) values (0, 'John Smith', 'john.smith@mailinator.com', '2125551212');

insert into repo_definition(id, name) values (1, 'Person');

insert into repo_attribute_definition(id, name, type, owner_repo_definition, value_repo_type, multi_value, mandatory) values (1, 'First name', 'STRING', 1, null, 0, 0);
insert into repo_attribute_definition(id, name, type, owner_repo_definition, value_repo_type, multi_value, mandatory) values (2, 'Last name', 'STRING', 1, null, 0, 0);
insert into repo_attribute_definition(id, name, type, owner_repo_definition, value_repo_type, multi_value, mandatory) values (3, 'Age', 'INTEGER', 1, null, 0, 0);
insert into repo_attribute_definition(id, name, type, owner_repo_definition, value_repo_type, multi_value, mandatory) values (4, 'Mother', 'REPO_ITEM', 1, 1, 0, 0);
