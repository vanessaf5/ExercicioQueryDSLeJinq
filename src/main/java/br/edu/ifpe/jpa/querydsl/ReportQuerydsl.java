package br.edu.ifpe.jpa.querydsl;

import static com.querydsl.core.group.GroupBy.*;

import java.util.Date;
import java.util.List;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;

import br.edu.ifpe.jpa.IReportGenerator;
import br.edu.ifpe.jpa.entities.QAccount;
import br.edu.ifpe.jpa.entities.QClient;

public class ReportQuerydsl implements IReportGenerator {

	static EntityManagerHelper helper = EntityManagerHelper.getInstance();
	
	@Override
	public List<String> getClientNames(Date initialDate, Date finalDate) {
		QClient client = QClient.client;
		
		return helper.execute(query ->
			query
				.select(client.name)
				.from(client)
				.where(client.birthDate.after(initialDate).and(client.birthDate.before(finalDate)))
				.orderBy(client.birthDate.asc())
				.fetch()
			);
	}
	
	/**
	 * Retorna valor total em dinheiro que o cliente possui no banco, somando todas as suas contas
	 * 
	 * @param email e-mail do cliente
	 * @return Retorna a soma dos saldos de todas as contas do cliente
	 */
	@Override
	public double getClientTotalCash(String email) {
            QAccount account = QAccount.account;
          return  helper.execute(query
            -> query
                    .select(account.balance.sum())
                    .from(account)
                    .where(account.client.email.eq(email))
                    .groupBy(account.balance)
                    .fetchOne()
                    

                    
            );
	}
        /**
	 * Retorna uma lista (ranking) com os e-mails dos clientes que possuem mais dinheiro no banco, somando 
	 * todas suas contas. Além disso, a lista é ordenada de acordo com a quantidade de dinheiro que 
	 * o cliente possui. Caso o valor seja igual, é considerada a ordem alfabetica.
	 *  
	 * @param agency agência a ser considerada
	 * @param rankingSize quantidade de clientes que devem constar no ranking
	 * @return Retorna uma lista com os e-mails dos clientes que possuem o maior valor guardado no banco. 
	 */
	@Override
	public List<String> getBestClientsEmails(String agency, int rankingSize) {
            QAccount account = QAccount.account;
            
         return  helper.execute(query
            -> query
                    .select(account.client.email)
                    .from(account)
                    .where(account.agency.eq(agency))
                    .groupBy(account.balance.sum())
                    .orderBy(account.balance.asc())
                    .limit(rankingSize)
                    .fetch()
                 
          
            
            );
            
	}
}
