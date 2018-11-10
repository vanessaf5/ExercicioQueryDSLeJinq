package br.edu.ifpe.jpa.jinq;

import java.util.Date;
import java.util.List;

import br.edu.ifpe.jpa.IReportGenerator;
import br.edu.ifpe.jpa.entities.Account;
import br.edu.ifpe.jpa.entities.Client;
import java.util.stream.Collectors;

public class ReportJinq implements IReportGenerator {

    static EntityManagerHelper helper = EntityManagerHelper.getInstance();

    @Override
    public List<String> getClientNames(Date initialDate, Date finalDate) {
        return helper.execute(Client.class, streams
                -> streams
                        .where(client -> client.getBirthDate().after(initialDate))
                        .where(client -> client.getBirthDate().before(finalDate))
                        .select(Client::getName)
                        .sortedBy(client -> client)
                        .toList()
        );
    }

    /**
     * Retorna valor total em dinheiro que o cliente possui no banco, somando
     * todas as suas contas
     *
     * @param email e-mail do cliente
     * @return Retorna a soma dos saldos de todas as contas do cliente
     */
    @Override
    public double getClientTotalCash(String email) {

        return helper.execute(Account.class,
                streams
                -> streams
                        .where(account -> account.getClient().getEmail().equals(email))
                        .sumDouble(Account::getBalance)
        );

    }

    /**
     * Retorna uma lista (ranking) com os e-mails dos clientes que possuem mais
     * dinheiro no banco, somando todas suas contas. Além disso, a lista é
     * ordenada de acordo com a quantidade de dinheiro que o cliente possui.
     * Caso o valor seja igual, é considerada a ordem alfabetica.
     *
     * @param agency agência a ser considerada
     * @param rankingSize quantidade de clientes que devem constar no ranking
     * @return Retorna uma lista com os e-mails dos clientes que possuem o maior
     * valor guardado no banco.
     */
    @Override
    public List<String> getBestClientsEmails(String agency, int rankingSize) {

        List<String> emails = null;
        emails.addAll(
                helper.execute(Account.class,
                        streams
                        -> streams
                                .sortedBy(Account::getBalance)
                                .limit(rankingSize)
                                .where(a -> a.getAgency().equals(agency))
                                .select(a -> a.getClient().getEmail())
                                .sumDouble(Account::getBalance)
                )
        );

        return emails;

    }
}
