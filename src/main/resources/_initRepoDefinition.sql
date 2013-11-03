-- WTF with this file?
insert into repo_definition(id, name) values (1, 'Person');

insert into repo_attribute_definition(id, name, type, repo_definition) values (1, 'First name', 'STRING', 1);
insert into repo_attribute_definition(id, name, type, repo_definition) values (2, 'Last name', 'STRING', 1);
insert into repo_attribute_definition(id, name, type, repo_definition) values (3, 'Age', 'INTEGER', 1);
