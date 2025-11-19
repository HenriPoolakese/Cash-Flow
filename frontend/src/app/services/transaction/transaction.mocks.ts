import { ExternalTransfersDTO, InternalTransfersDTO } from "./transaction-dto.model";

export const mockInternalTransactions: InternalTransfersDTO = {
  ownerNodes: [
    { customer_id: '1', customer_name: 'Company A', country: 'EST' },
    { customer_id: '2', customer_name: 'Company B', country: 'EST' },
    { customer_id: '3', customer_name: 'Head Office', country: 'EST' }
  ],
  ownerAndAccountLink: [
    {
      source: '1',
      target: 'EE121212121212121212',
      label: 'Internal Transfer',
      amount: 1500,
      currency: 'EUR'
    },
    {
      source: '2',
      target: 'EE321282973364859699',
      label: 'Internal Transfer',
      amount: 2750,
      currency: 'EUR'
    },
    {
      source: '3',
      target: 'EE893704004405320130',
      label: 'Internal Transfer',
      amount: 3200,
      currency: 'EUR'
    }
  ],
  accountNodes: [
    {
      customer_id: '1',
      customer_name: 'Company A',
      customer_type: 'Corp',
      customer_iban: 'EE121212121212121212',
      label: 'Company A',
      country: 'EST',
      currency: 'EUR'
    },
    {
      customer_id: '2',
      customer_name: 'Company B',
      customer_type: 'Corp',
      customer_iban: 'EE321282973364859699',
      label: 'Company B',
      country: 'EST',
      currency: 'EUR'
    },
    {
      customer_id: '3',
      customer_name: 'Head Office',
      customer_type: 'Corp',
      customer_iban: 'EE893704004405320130',
      label: 'Head Office',
      country: 'EST',
      currency: 'EUR'
    }
  ],
  accountLinks: [
    {
      source: 'EE121212121212121212',
      target: 'EE321282973364859699',
      label: 'Internal Transfer',
      amount: 1500,
      currency: 'EUR'
    },
    {
      source: 'EE321282973364859699',
      target: 'EE893704004405320130',
      label: 'Internal Transfer',
      amount: 2750,
      currency: 'EUR'
    },
    {
      source: 'EE893704004405320130',
      target: 'EE121212121212121212',
      label: 'Internal Transfer',
      amount: 3200,
      currency: 'EUR'
    }
  ]
};

export const mockExternalTransactions: ExternalTransfersDTO = {
  nodes: [
    { id: 1, name: 'Company A', country: 'EST', type: 'company', iban: null, currency: null },
    { id: 4, name: 'Supplier X', country: 'USA', type: 'company', iban: null, currency: null },
    { id: 5, name: 'Client Y', country: 'UK', type: 'company', iban: null, currency: null },
    { id: 6, name: 'International Partner Z', country: 'GER', type: 'company', iban: null, currency: null }
  ],
  groupLinks: [
    {
      source: '1',
      target: '4',
      label: 'Group Link',
      amount: 5000,
      currency: 'USD'
    },
    {
      source: '1',
      target: '5',
      label: 'Group Link',
      amount: 2300,
      currency: 'GBP'
    },
    {
      source: '6',
      target: '1',
      label: 'Group Link',
      amount: 4200,
      currency: 'EUR'
    }
  ],
  debitLinks: [
    {
      source: '1',
      target: '4',
      label: 'Foreign Transfer',
      amount: 5000,
      currency: 'USD'
    },
    {
      source: '1',
      target: '5',
      label: 'Foreign Transfer',
      amount: 2300,
      currency: 'GBP'
    },
    {
      source: '6',
      target: '1',
      label: 'Foreign Transfer',
      amount: 4200,
      currency: 'EUR'
    }
  ],
  creditLinks: [
    {
      source: '4',
      target: '1',
      label: 'Incoming Payment from Supplier X',
      amount: 2500,
      currency: 'USD'
    },
    {
      source: '5',
      target: '1',
      label: 'Incoming Payment from Client Y',
      amount: 1500,
      currency: 'GBP'
    },
    {
      source: '6',
      target: '1',
      label: 'Incoming Payment from International Partner Z',
      amount: 3400,
      currency: 'EUR'
    }
  ]
};
